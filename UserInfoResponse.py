class UserInfoResponse:
    def __init__(self,username):
        self.username = username

    def serialize(self):
        return {
            'username': self.username
        }