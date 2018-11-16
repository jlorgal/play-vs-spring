from django.conf.urls import url
from rest_framework_mongoengine import routers as merouters
from project.appname.views import PostViewSet
 
merouter = merouters.DefaultRouter()
merouter.register(r'posts', PostViewSet)
 
urlpatterns = [
 
]
 
urlpatterns += merouter.urls
