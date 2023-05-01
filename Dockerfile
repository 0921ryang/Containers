# TODO: Add base image
# TODO: Set workdir
# TODO: Copy the compiled jar
# TODO: Copy the start.sh script
# TODO: Make start.sh executable
# TODO: Set the start command
FROM openjdk:17-bullseye
WORKDIR /app
COPY start.sh .
RUN chmod 770 start.sh
CMD ./start.sh
EXPOSE 3000