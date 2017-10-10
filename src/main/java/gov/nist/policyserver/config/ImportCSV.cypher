match(n) detach delete n;
USING PERIODIC COMMIT 10000 LOAD CSV WITH HEADERS FROM "file:///test.csv" AS row create({id:TOINT(row.id), name:row.name, type:row.type});
match(n) where n.type='C' set n:C:node;
match(n) where n.type='PC' set n:PC:node;
match(n) where n.type='OA' set n:OA:node;
match(n) where n.type='O' set n:O:node;
match(n) where n.type='UA' set n:UA:node;
match(n) where n.type='U' set n:U:node;
match(n) where n.type='D' set n:D:denies, n.denyId=n.id with n remove n.id;
match(n) where n.type='DR' set n:DR:denies with n remove n.id;
match(n) where n.type='DS' set n:DS:denies with n remove n.id;
match(n) set n.testProp=n.id;
create constraint on (n:node) assert n.id is UNIQUE;

match(a:PC{name:'RBAC'}), (b) where b.name contains 'Grp' create (b)-[:assigned_to]->(a);
match(a:O), (b:OA) where a.name starts with b.name create (a)-[:assigned_to]->(b);
match(a:UA{id:33}), (b:PC{name:'EMP_PC'}) create (a)-[:assigned_to]->(b);
match(a:UA{id:33}), (b:U) create (b)-[:assigned_to]->(a);
match(a:OA{id:35}), (b:PC{name:'EMP_PC'}) create (a)-[:assigned_to]->(b);



match(a:OA{id:35}), (b:OA) where b.name ends with '_5555555555' create (b)-[:assigned_to]->(a);

match(a:OA), (b:OA) with a,b where split(a.name, '_')[1] = split(b.name, '_')[1] and b.name starts with 'Grp' and a.id <> b.id and a.name starts with 'er' create (a)-[:assigned_to]->(b);
match(a:U), (b:UA) with a,b where split(a.name, '_')[1] = split(b.name, '_')[1] and b.name starts with 'Grp' and a.name starts with 'u' create (a)-[:assigned_to]->(b);

match(a:O), (b:OA) where split(a.name, '_')[3]=split(b.name, '_')[1] and b.name ends with '_5555555555' create (a)-[:assigned_to]->(b);



match(a:UA{id:33}), (b:OA{id:35}) create (a)-[:association{operations:['r','w'], inherit:'true'}]->(b);
match(a:UA), (b:OA) where  a.name starts with 'Grp' and b.name starts with 'Grp' and split(a.name, '_')[1] = split(b.name, '_')[1] create (a)-[:association{operations:['r'], inherit:'true'}]->(b);

match(a) where a.name starts with 'er' or a.name starts with 'col' or (a.name='Employees' and a.type='OA') set a.namespace='Employees';