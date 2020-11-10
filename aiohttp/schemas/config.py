from marshmallow import Schema, fields


class RouteAPIConfigSchema(Schema):
    method_name = 'method'
    path_name = 'path'
    handler_name = 'handler'
    handler_args_name = 'handler_args'

    method = fields.Str(required=True)
    path = fields.Str(required=True)
    handler = fields.Str(required=True)
    handler_args = fields.Dict(required=False)
