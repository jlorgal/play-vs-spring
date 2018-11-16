from mongoengine import Document, EmbeddedDocument, fields
 
class Post(Document):
    title = fields.StringField(required=True)
    body = fields.StringField(required=True, null=True)
