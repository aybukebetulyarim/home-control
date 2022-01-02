from flask import Flask, request, make_response
from flask_pymongo import PyMongo
from flask import jsonify
import jwt
import pymongo
from datetime import datetime, timedelta
from functools import wraps
from bson.objectid import ObjectId
from bson.timestamp import Timestamp
import datetime as dt
import time
import datetime

app = Flask(__name__)
app.config["MONGO_URI"] = "mongodb://admin:uJ7NwsbFSHUsEGG@cluster0-shard-00-00.d1esl.mongodb.net:27017,cluster0-shard-00-01.d1esl.mongodb.net:27017,cluster0-shard-00-02.d1esl.mongodb.net:27017/smarthome?ssl=true&replicaSet=atlas-6e4ray-shard-0&authSource=admin&w=majority"
app.config['SECRET_KEY'] = 'smarthomeAybuke**'
mongo = PyMongo(app)

class LoginResponse:
    def __init__(self, token):
        self.token = token

    def serialize(self):
        return {
            'token': self.token
        }

class SensorInfoResponse:
    def __init__(self,response):
        self.temperature = response['temperature']
        self.motion      = response['motion']
        self.gas         = response['gas']
        self.humidity    = response['humidity']
        self.timestamp   = response['timestamp']

    def serialize(self):
        return {
        'temperature': self.temperature,
        'motion'     : self.motion,
        'gas'        : self.gas,
        'humidity'   : self.humidity,
        'timestamp'  : self.timestamp
        }

class UserInfoResponse:
    def __init__(self,username):
        self.username = username

    def serialize(self):
        return {
            'username': self.username
        }

def getNowTime():
    timestamp = Timestamp(int(dt.datetime.today().timestamp()), 1)
    return timestamp

def current_milli_time():
    return round(time.time() * 1000)
    
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        # jwt is passed in the request header
        if 'Authorization' in request.headers:
            token = request.headers['Authorization']
            token = token.split(" ")[1]
        # return 401 if token is not passed
        if not token:
            return jsonify({'message' : 'Token is missing !!'}), 401
  
        try:
            # decoding the payload to fetch the stored details
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms="HS256")

            current_user = mongo.db.users.find_one({"_id": ObjectId(data['id'])})
            print(current_user)
                
        except:
            return jsonify({'message' : 'Token is invalid !!'}), 401
        # returns the current logged in users contex to the routes
        return f(current_user, *args, **kwargs)
  
    return decorated


@app.route("/login", methods=["POST"])
def login():
    username = request.json["username"]
    password = request.json["password"]

    user = mongo.db.users.find_one({"username": username, "password":password})

    print(user["_id"])

    if not user:
        return make_response(jsonify({"message":"Böyle bir kullanıcı bulunmuyor."}), 404)

    if user["password"] == password:
        
        token = jwt.encode({
            'id': str(user["_id"]),
            'exp' : datetime.utcnow() + timedelta(minutes = 30)
        }, app.config['SECRET_KEY'])

        response = LoginResponse(token)

        return make_response(jsonify(response.serialize()),200)

    else:
        return make_response(jsonify({"message":"Kullanıcı adınız veya şifreniz hatalı."}),200)


@app.route("/register", methods=["POST"])
def register():
    username = request.json["username"]
    password = request.json["password"]

    user = {
        'username': username,
        'password': password
        }

    mongo.db.users.insert_one(user)

    return make_response(jsonify({"message":"User created."}),200)


@app.route("/sensor", methods=["GET"])
def getSensor():
    response = mongo.db.sensors.find().sort('timestamp',  pymongo.DESCENDING).limit(1)
    data = None
    for x in response:
        data = x

    responseData = SensorInfoResponse(data)
    print(responseData)
    return make_response(jsonify(responseData.serialize()),200)


@app.route("/sensor", methods=["POST"])
def setSensor():
    temperature = request.json["temperature"]
    motion      = request.json["motion"]
    gas         = request.json["gas"]
    humidity    = request.json["humidity"]
    timestamp   = current_milli_time()

    sensorData = {
        'temperature': temperature,
        'motion'     : motion,
        'gas'        : gas,
        'humidity'   : humidity,
        "timestamp"  : timestamp
    }
    mongo.db.sensors.insert_one(sensorData)

    return make_response(jsonify({"message":"Sensor data sended database."}))

@app.route("/user", methods = ['POST'])
def home_page():

    data = request.json

    users = {
        'firstname':data.get("name"),
        'lastname': data.get("surname")
    }

    try:
        print(mongo.db.server_info())
    except Exception:
        print("Unable to connect to the server.")

    
    mongo.db.users.insert_one(users)

    online_users = mongo.db.users.find()
    print(online_users)
    temp = [get_data(i) for i in online_users]
    return jsonify(temp)

def get_data(data):
     data['_id'] = str(data['_id'])
     return data



if __name__ == "__main__":
    app.run(debug=True)
