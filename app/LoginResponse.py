class LoginResponse:
    def __init__(self, token):
        self.token = token

    def serialize(self):
        return {
            'token': self.token
        }