DROP TABLE city;
CREATE TABLE city(
    Nombre_ciudad VARCHAR2(100) PRIMARY KEY
);


-----------------------------------------------------
-----------------------------------------------------
INSERT INTO city VALUES('Cali');
INSERT INTO city VALUES('Medellín');
INSERT INTO city VALUES('Bogotá');
-----------------------------------------------------
-----------------------------------------------------

DROP TYPE ventas_tipo FORCE;
CREATE OR REPLACE TYPE ventas_tipo
AS OBJECT( 
    x NUMBER(4), 
    y NUMBER(4),
    v NUMBER(10)
);
/

CREATE OR REPLACE TYPE
ventas_anidada AS TABLE OF ventas_tipo;
/

CREATE OR REPLACE TYPE vendedor_tipo AS 
OBJECT(
    CodigoVendedor NUMBER(5),
    Ciudad VARCHAR2(100),
    ventas ventas_anidada);
/

DROP TABLE VVCITY;
CREATE TABLE VVCITY OF vendedor_tipo
(
    PRIMARY KEY(CodigoVendedor, Ciudad)
) 
NESTED TABLE ventas STORE AS ventas_almacenadas
(
    (PRIMARY KEY(NESTED_TABLE_ID, x, y))
);

-----------------------------------------------------
-----------------------------------------------------
INSERT INTO VVCITY VALUES(  5, 'Cali',
                            ventas_anidada(
                                ventas_tipo(4, 10, 10),
                                ventas_tipo(30, 5, 50)
                                )
);

SELECT CodigoVendedor, Ciudad, t2.*
FROM VVCITY t, TABLE(t.ventas) t2;

INSERT INTO VVCITY VALUES(  5, 'Bogotá',
                            ventas_anidada(
                                ventas_tipo(50, 10, 10),
                                ventas_tipo(35, 5, 50)
                                )
);

INSERT INTO TABLE(  SELECT ventas
                    FROM VVCITY
                    WHERE CodigoVendedor = 9 AND Ciudad='Cali')
VALUES(4, 10, 25);

UPDATE TABLE(   SELECT ventas 
                FROM VVCITY
                WHERE CodigoVendedor = 5 AND Ciudad='Cali')
SET v = v + 800
WHERE x=4 AND y=10;

SELECT t2.*
FROM VVCITY t1, TABLE(t1.ventas) t2
WHERE t1.ciudad = 'cali';

select * from vvcity;
-----------------------------------------------------
-----------------------------------------------------