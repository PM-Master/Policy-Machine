match(n) detach delete n;
USING PERIODIC COMMIT 10000 LOAD CSV WITH HEADERS FROM "file:///medrecs.csv" AS row create({id:TOINT(row.id), name:row.name, patient:row.patient, container:row.container, value:row.value, type:row.type, row:TOINT(row.row), column:TOINT(row.column)});
match(n) where n.type='C' set n:C:node;
match(n) where n.type='PC' set n:PC:node;
match(n) where n.type='OA' set n:OA:node;
match(n) where n.type='O' set n:O:node;
match(n) where n.type='UA' set n:UA:node;
match(n) where n.type='U' set n:U:node;

match(n{name:'Health Care Records'}), (m{name:'HC_PC'}) create (n)-[:assigned_to]->(m);
match(n{name:'Health Care Records'}), (m{name:'RBAC'}) create (n)-[:assigned_to]->(m);

match(a), (b) where b.container=a.name create (a)<-[:assigned_to]-(b);
match(a), (b) where b.name=a.name and b.patient is not null and a.patient is null create (a)<-[r:assigned_to]-(b);

match(a:UA), (b:U) where a.name = b.name create (a)-[:assigned_to]->(b);

match(a:UA{name:'doctor'}), (b:OA{name:'name'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'DOB'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'treatment history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'symptoms history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'prescription history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'family history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'diagnoses history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'metabolic panel'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);

match(a:UA{name:'patient'}), (b:OA) where b.name ends with 'rec' create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'patient'}), (b:OA{name:'hp'}) create (a)-[:association{operations:['w'], inherit:'true'}]->(b);
match(a:UA{name:'patient'}), (b:OA{name:'wp'}) create (a)-[:association{operations:['w'], inherit:'true'}]->(b);
match(a:UA{name:'patient'}), (b:OA{name:'cp'}) create (a)-[:association{operations:['w'], inherit:'true'}]->(b);
match(a:UA{name:'patient'}), (b:OA{name:'email'}) create (a)-[:association{operations:['w'], inherit:'true'}]->(b);
match(a:UA{name:'patient'}), (b:OA{name:'ICE name'}) create (a)-[:association{operations:['w'], inherit:'true'}]->(b);
match(a:UA{name:'patient'}), (b:OA{name:'ICE phone'}) create (a)-[:association{operations:['w'], inherit:'true'}]->(b);

match(a:UA{name:'emt'}), (b:OA{name:'name'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'ICE name'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'ICE phone'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'gender'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'DOB'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'treatment history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'symptoms history'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'prescription history'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'family history'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'diagnoses history'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'emt'}), (b:OA{name:'metabolic panel'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);

match(a:UA{name:'admin'}), (b:OA{name:'name'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'admin'}), (b:OA{name:'Patient ID'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'admin'}), (b:OA{name:'billing info'}) create (a)-[:association{operations:['r', 'w'], inherit:'true'}]->(b);


match(a:UA{name:'patient'}), (b:OA{name:'patient-home'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);
match(a:UA{name:'doctor'}), (b:OA{name:'doctor-home'}) create (a)-[:association{operations:['r'], inherit:'true'}]->(b);