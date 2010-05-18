#!/bin/sh
curl -v -X POST --data-binary @create_order.json -H"Conteapplication/json" -H"Accept: application/json" http://localhost:9998/order/
