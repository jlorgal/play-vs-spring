from django.shortcuts import render
 
from rest_framework_mongoengine import viewsets as meviewsets
from project.appname.serializers import PostSerializer
from project.appname.models import Post
 
class PostViewSet(meviewsets.ModelViewSet):
    lookup_field = 'id'
    queryset = Post.objects.all()
    serializer_class = PostSerializer
