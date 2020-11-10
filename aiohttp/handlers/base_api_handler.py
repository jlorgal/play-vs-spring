import configparser


class BaseAPIHandler(object):
    SERVER_SECTION_NAME = 'server'

    def __init__(self, cfg: configparser.ConfigParser):
        self.cfg = cfg
