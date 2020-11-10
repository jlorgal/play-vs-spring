import ast
import configparser
import sys
from typing import List

from aiohttp import web

from schemas.config import RouteAPIConfigSchema
from utils.import_handler import APIComponentsImportHandler


class RoutesHandler(object):
    CFG_SECTION_NAME = 'api-routes'
    ROUTES = 'routes'

    @staticmethod
    def generate_routes(cfg: configparser.ConfigParser) -> List:
        """
        Generate api routes from config
        """
        return_value = []
        routes_list = None

        # TODO: apply LBYL
        try:
            routes_list = ast.literal_eval(cfg[RoutesHandler.CFG_SECTION_NAME][RoutesHandler.ROUTES])
        except SyntaxError:
            print('config syntax error %s' % 'evaluating routes', file=sys.stderr)

        if isinstance(routes_list, list):
            if len(routes_list) > 0:
                for raw_route in routes_list:
                    route_api_config_schema = RouteAPIConfigSchema()
                    validation_result = route_api_config_schema.validate(raw_route)
                    if not validation_result:
                        route = route_api_config_schema.dump(raw_route)
                        handler = APIComponentsImportHandler.import_handler(
                            cfg=cfg, handler_path=route.data[RouteAPIConfigSchema.handler_name],
                            handler_args=route.data.get(RouteAPIConfigSchema.handler_args_name, {}))

                        if handler:
                            route.data[RouteAPIConfigSchema.handler_name] = handler
                            route.data.pop(RouteAPIConfigSchema.handler_args_name, None)
                            return_value.append(web.route(**route.data))
                    else:
                        print('Var %s has an invalid format --> %s' % ('route', str(validation_result)),
                              file=sys.stderr)
            else:
                print('var routes_list was empty', file=sys.stderr)
        else:
            print('Var %s has type --> %s' % ('routes_list', type(routes_list)), file=sys.stderr)

        return return_value
