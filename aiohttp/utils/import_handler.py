import configparser
import importlib
import sys
from typing import Type, Dict, Any

from handlers.base_api_handler import BaseAPIHandler


class ImportHandler(object):

    @staticmethod
    def import_meta(class_path: str) -> Type:
        meta_class_obj = None
        module_path, class_name = class_path.rsplit('.', 1)
        if module_path and class_name:
            package_name, module_name = module_path.rsplit('.', 1)
            module_obj = importlib.import_module('.' + module_name, package=package_name)
            if module_obj and hasattr(module_obj, class_name):
                meta_class_obj = getattr(module_obj, class_name)
            else:
                print('Does not exist the class to import: %s' % class_path, file=sys.stderr)

        if meta_class_obj is None:
            print('Does not exist the class to import: %s' % class_path, file=sys.stderr)

        return meta_class_obj

    @staticmethod
    def import_class(class_path: str, class_args: Dict[str, Any] = None):
        class_obj = None

        meta_class_obj = ImportHandler.import_meta(class_path=class_path)
        if meta_class_obj:
            if class_args is not None:
                if isinstance(class_args, dict) and all(isinstance(key, str) for key in class_args):
                    class_params = class_args
                else:
                    raise ValueError('Class args must be a dict and keywords must be strings')
            else:
                class_params = {}
            class_obj = meta_class_obj(**class_params)

        if class_obj is None:
            print('%s unable to import' % class_path, file=sys.stderr)

        return class_obj

    @staticmethod
    def import_handler(handler_path: str, handler_args: Dict[str, Any] = None):
        handler = None
        class_path, method_name = handler_path.split(':')
        if class_path and method_name:
            class_obj = ImportHandler.import_class(class_path=class_path, class_args=handler_args)
            if hasattr(class_obj, method_name):
                handler = getattr(class_obj, method_name)
            else:
                print('%s unable to import' % handler_path, file=sys.stderr)

        if handler is None:
            print('%s unable to import' % handler_path, file=sys.stderr)

        return handler


class APIComponentsImportHandler(object):
    @staticmethod
    def import_handler(cfg: configparser.ConfigParser, handler_path: str,
                       handler_args: Dict[str, Any] = None) -> BaseAPIHandler:
        extra_args = {'cfg': cfg}
        if handler_args:
            if isinstance(handler_args, dict):
                extra_args['handler_args'] = handler_args
            else:
                raise ValueError('handler_args has an invalid type -> ' + handler_args.__class__.__name__)

        handler: BaseAPIHandler = ImportHandler.import_handler(handler_path=handler_path, handler_args=extra_args)

        if handler is None:
            print('Unable to import --> %s' % handler_path, file=sys.stderr)

        return handler
