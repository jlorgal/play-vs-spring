import configparser

from aiohttp import web
from bson import ObjectId, json_util
from motor import motor_asyncio

from handlers.base_api_handler import BaseAPIHandler


class DummyAPIHandler(BaseAPIHandler):

    def __init__(self, cfg: configparser.ConfigParser):
        super().__init__(cfg)
        self.motor_client = motor_asyncio.AsyncIOMotorClient(self.cfg['mongo']['uri'])
        self.mongo_db_name = self.cfg['mongo']['database']
        self.mongo_collection_name = self.cfg['mongo']['collection']
        self.db = self.motor_client[self.mongo_db_name]

    async def post(self, request: web.Request):
        body = await request.json()
        result = await self.db[self.mongo_collection_name].insert_one(body)

        body['_id'] = result.inserted_id
        return web.json_response(status=200, text=json_util.dumps(body))

    async def query(self, request: web.Request):
        doc_id = request.match_info['doc_id']
        document = await self.db[self.mongo_collection_name].find_one({'_id': ObjectId(doc_id)})
        response = web.json_response(status=200, text=json_util.dumps(document))

        return response
