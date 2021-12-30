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