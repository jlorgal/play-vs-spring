import argparse
import asyncio
import configparser
import os
import socket
import sys

import aiotask_context
import uvloop
from aiohttp import web

from routes.routes import RoutesHandler

DEFAULT_CONFIG_NAME = 'default_api'
DEFAULT_CONFIG_ENCODING = 'utf-8'
DEFAULT_PORT = 8000


def parse_args():
    parser = argparse.ArgumentParser(description='HTTP server')

    g1 = parser.add_argument_group('Configuration')
    g1.add_argument('--config', '-c', type=str, help='configuration path', required=True)
    g1.add_argument('--port', '-P', type=int, help='listen port')

    g2 = parser.add_argument_group('Behaviour')
    g2.add_argument('--debug', '-d', action='store_true', default=False)

    return parser.parse_args()


def validate_config(cfg: configparser.ConfigParser) -> bool:
    validation_status = True

    mandatory_sections = [RoutesHandler.CFG_SECTION_NAME]
    if cfg:
        for mandatory_section in mandatory_sections:
            if not (mandatory_section in cfg):
                print('Error: invalid app config: missing [%s] section' % mandatory_section, cfg)
                validation_status = False
                break
    else:
        print('Error: app config was None')
        validation_status = False

    return validation_status


def get_config(force_port: int = None, path: str = __file__):
    """
    Get the configuration object
    """
    cfg = configparser.ConfigParser()
    cfg.read(filenames=path, encoding='utf-8')

    if validate_config(cfg=cfg):
        if force_port is not None:
            cfg['server']['port'] = str(force_port)
        elif 'port' not in cfg['server']:
            cfg['server']['port'] = str(DEFAULT_PORT)
        # Add the local path for the current application
        cfg['server']['app_path'] = path

    else:
        cfg = None

    return cfg


def generate_app(cfg: configparser.ConfigParser):
    app = None
    asyncio.set_event_loop_policy(uvloop.EventLoopPolicy())
    loop = asyncio.get_event_loop()
    loop.set_task_factory(aiotask_context.task_factory)

    routes = RoutesHandler.generate_routes(cfg=cfg)
    n_routes = len(routes)
    print('Generating %d routes' % n_routes)

    if n_routes > 0:

        app = web.Application(loop=loop)
        app.add_routes(routes)
    else:
        print('variable routes was empty')

    return app


def start_app(config_path: str, port: int = None):
    cfg = get_config(force_port=port, path=os.path.abspath(os.path.normpath(config_path)))
    if cfg:
        app_port = int(cfg['server']['port'])
        app = generate_app(cfg=cfg)
        if app is not None:
            sock = None
            socket_address = None
            if dict(cfg['server']).get('unix_socket_name'):
                app_port = None
                unix_socket_directory = cfg['server']['unix_socket_directory']
                unix_socket_name = cfg['server']['unix_socket_name']
                os.makedirs(unix_socket_directory, exist_ok=True)
                sock = socket.socket(family=socket.AF_UNIX, type=socket.SOCK_STREAM)
                socket_address = os.path.join(unix_socket_directory, unix_socket_name)
                if os.path.exists(socket_address):
                    os.unlink(socket_address)
                sock.bind(socket_address)
                os.chmod(path=socket_address, mode=760)

            message = 'App running on unix socket %s' % socket_address
            if app_port is not None:
                message = 'Application is now running on port %s' % str(app_port)

            print(message)
            web.run_app(app, port=app_port, sock=sock)
        else:
            print('app variable was None. Unable to run application', file=sys.stderr)


def main():
    args = parse_args()
    start_app(port=args.port, config_path=args.config)


if __name__ == '__main__':
    main()  # pragma: no cover
