#!/bin/sh
curl -v -X PUT --data-binary @update_order.json -H"Content-type: application/json" -H"Accept: application/json" http://localhost:9998/order/2
