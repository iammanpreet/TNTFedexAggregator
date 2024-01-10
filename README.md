**Fedex Aggregation Application**

**Overview**

This git repository is basically containing the implementation of the Aggregation Service. This service combines the network calls of all three external clients named as Pricing, Track and Shipments, and optimising the network traffic.
This api also takes into consideration the SLA timeouts for the external clients as well as SLA timeout for the Aggregation Service.

**Prerequisites**

Make sure you have Docker, java and maven installed on your machine.

Below are the steps to run the application

**Clone git repository:**

git clone https://github.com/iammanpreet/TNTFedexAggregator.git
cd TNTFedexAggregator

Build the Docker image:

docker build -t aggregation-service .

**Run the Docker container:**

docker run -p 8081:8081 --name aggregation-service aggregation-service

**Aggregation API:**

GET http://localhost:8081/aggregation?pricing=NL,CN&track=109347263,123456891&shipments=109347263,123456891

Returns a consolidated response from 3 external clients i-e Pricing, Tracking, and Shipments.

**Stories Implementation**

**AS-1: Single Network Call**

This story is basically to consolidate the response of all the external clients i-e pricing, track and shipment into one network call.

**AS-2: Throttling and Batching**
In this story, aggregation service api calls are throttled and batched to prevent overloading of Aggregation service and external clients. Each external client has a cap of 5 calls, and once 5 calls for any external client is queued, the request will be consolidated and triggered..

**AS-3: Scheduler**
In this story, Service calls are scheduled periodically to a delay of 5 sec for the message, if the queue has already processed the message via batch of cap of 5, then this schedule wont do anything, else it will combine the records in the queue and trigger api call.

**Docker Hub Images**
The external client services are available as Docker images on Docker Hub:

Pricing API
Tracking API
Shipments API

**Testing**

Test cases are created in the src/test/java folder of the application.
We can run mvn test in order to execute the test suits.

**Contributors**
Manpreet kaur 
