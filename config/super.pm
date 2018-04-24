{
   "nodes":[
      {
         "id":-1,
         "name":"Super PC",
         "type":"PC"
      },
      {
         "id":-2,
         "name":"PM",
         "type":"OA",
         "properties": [
            {
                "key": "namespace",
                "value": "connector"
            }
         ]
      },
      {
         "id":-3,
         "name":"Super",
         "type":"UA"
      },
      {
         "id":-4,
         "name":"super",
         "type":"U",
         "properties": [
            {
                "key": "password",
                "value": "super"
            }
         ]
      }
   ],
   "assignments":[
      {
         "child":-2,
         "parent":-1
      },
      {
         "child":-3,
         "parent":-1
      },
      {
         "child":-4,
         "parent":-3
      }
   ],
   "associations":[
      {
         "ua":-3,
         "target":-2,
         "ops":[
            "*"
         ],
         "isInherit":true
      }
   ]
}