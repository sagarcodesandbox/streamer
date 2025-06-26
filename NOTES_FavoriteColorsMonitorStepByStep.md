Steps to Monitor Favorite Color Stream

Prerequisites
✅ All services are running and healthy
✅ Count values issue is fixed
✅ Streams service is in RUNNING state

Real-Time Monitoring Setup

Step 1: Open Multiple Terminals
Open 4 separate terminal windows/tabs and navigate to the project directory in each:

cd /Users/sagar/Projects/UltraProduction/Streamer

docker exec kafka-broker /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic favorite-colors-raw

docker exec kafka-broker /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic persons-and-colors-topic

# Test different color combinations
curl -X POST -H "Content-Type: application/json" -d '{"myFavoriteColor":"alice,blue"}' http://localhost:8080/api/favorite-colors/create

curl -X POST -H "Content-Type: application/json" -d '{"myFavoriteColor":"bob,red"}' http://localhost:8080/api/favorite-colors/create

curl -X POST -H "Content-Type: application/json" -d '{"myFavoriteColor":"charlie,green"}' http://localhost:8080/api/favorite-colors/create

curl -X POST -H "Content-Type: application/json" -d '{"myFavoriteColor":"alice,green"}' http://localhost:8080/api/favorite-colors/create

What You'll See

Terminal 1 (Raw):

{"myFavoriteColor":"alice,blue"}
{"myFavoriteColor":"bob,red"}
{"myFavoriteColor":"charlie,green"}

Terminal 2 (Intermediate):

blue
red
green
green

Terminal 3 (Aggregated Counts):

blue -> 1
red -> 1
green -> 1
green -> 2
Valid colors: green, blue, red (others will be filtered out)

