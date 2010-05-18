#!/bin/sh
curl -v -X POST --data-binary @create_order.json -H"Content-type: application/json" -H"Accept: application/json" http://localhost:9998/order/
