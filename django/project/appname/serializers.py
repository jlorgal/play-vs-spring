from rest_framework_mongoengine import serializers
from project.appname.models import Post
 
class PostSerializer(serializers.DocumentSerializer):
    class Meta:
        model = Post
        fields = '__all__'
