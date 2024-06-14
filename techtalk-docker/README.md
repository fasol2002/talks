# Docker optimization

> [!Mainly inspired by]
> [Docker build cache page](https://docs.docker.com/build/cache/)

```
sudo docker images
sudo docker rmi -f <imageid>

sudo docker history <imageid> (--no-trunc)

sudo docker build -t techtalk -f Dockerfile . && sudo docker run techtalk
sudo docker build -t techtalk-optim -f DockerfileOptim . && sudo docker run techtalk-optim


sudo docker system info
sudo docker system prune
 sudo docker image prune
 sudo docker container prune
 
 ```