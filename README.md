# Submit Order Request - Response Examples
## Request
```
{
    "clientReferenceCode" : "135",
    "description" : "test description",
    "orderItems" : [
        {
            "unitPrice" : "13.00",
            "units" : 2
        },
        {
            "unitPrice" : "18.00",
            "units" : 1
        }
    ]
}
```
## Response
```
{
    "id": "71c0d654-a6f9-41ce-8edb-c654e25f64a3",
    "status": "SUBMITTED",
    "description": "test description",
    "clientReferenceCode": "135",
    "totalAmount": 44.00,
    "itemCount": 2,
    "orderItems": [
        {
            "itemId": "9b9c184a-5a4a-48f6-804b-a6bf4af0796a",
            "units": 2,
            "unitPrice": 13.00,
            "totalPrice": 26.00
        },
        {
            "itemId": "d4b40d6d-4e74-4318-be7a-ac5ead168c21",
            "units": 1,
            "unitPrice": 18.00,
            "totalPrice": 18.00
        }
    ]
}
```
## Error Response
```
{
    "errorCode": "DUPLICATE_CLIENT_REF",
    "errorMessage": "Client reference code (clientReferenceCode) already exists"
}
```
# Fetch One Order Response Examples
## Response
```
{
    "id": "4dc8b4d2-cbcb-48a9-bd25-3c170120218f",
    "status": "SUBMITTED",
    "description": "test description",
    "clientReferenceCode": "12356",
    "totalAmount": 37.00,
    "itemCount": 2,
    "orderItems": [
        {
            "itemId": "d5e6ab2f-5219-476d-9f87-64561a5dca4f",
            "units": 2,
            "unitPrice": 13.00,
            "totalPrice": 26.00
        },
        {
            "itemId": "4e992c17-9d49-4a2c-b4db-ee020a289023",
            "units": 1,
            "unitPrice": 11.00,
            "totalPrice": 11.00
        }
    ]
}
```
## Error Response
```
{
    "errorCode": "INVALID_ORDER_ID",
    "errorMessage": "Order ID (orderId) does not exist"
}
```
# Fetch All Orders Response Example
```
[
    {
        "id": "e6211d64-7279-476a-be36-c4aa6253f736",
        "status": "SUBMITTED",
        "description": "test description",
        "clientReferenceCode": "1356",
        "totalAmount": 44.00,
        "itemCount": 2
    },
    {
        "id": "4dc8b4d2-cbcb-48a9-bd25-3c170120218f",
        "status": "SUBMITTED",
        "description": "test description",
        "clientReferenceCode": "12356",
        "totalAmount": 37.00,
        "itemCount": 2
    }
]
```

