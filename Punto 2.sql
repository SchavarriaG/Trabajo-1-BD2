--Se crea la tabla
DROP TABLE sucursal;
CREATE TABLE sucursal(
    cods VARCHAR2(500) PRIMARY KEY,
    nrosucdependientes NUMBER(1) NOT NULL CHECK (nrosucdependientes BETWEEN 0 AND 9)
);

--Se crea el trigger compuesto para inserción o cuando se actualiza el campo nrosucdependientes
CREATE OR REPLACE TRIGGER insercion_sucursal
    FOR INSERT OR UPDATE OF nrosucdependientes ON sucursal
        COMPOUND TRIGGER

    --Se crean las variables para llevar el registro
    codigo sucursal.cods%TYPE;
    dependencias_nuevas sucursal.nrosucdependientes%TYPE;
    dependencias_viejas sucursal.nrosucdependientes%TYPE;

    --Se inicializan las variables anteriores con los datos ingresados
    AFTER EACH ROW IS
    BEGIN
        --Para inserción
        IF INSERTING THEN
            codigo := :NEW.cods;
            dependencias_nuevas := :NEW.nrosucdependientes;
        --Para actualización
        ELSE
            codigo := :OLD.cods;
            dependencias_nuevas := :NEW.nrosucdependientes;
            dependencias_viejas := :OLD.nrosucdependientes;
        END IF;
    END AFTER EACH ROW;

    --Se realizan los cambios en la tabla
    AFTER STATEMENT IS
    BEGIN
        --Para inserción
        IF INSERTING THEN
            --se insertan las filas adicionales
            FOR i IN 1..dependencias_nuevas LOOP
                INSERT INTO sucursal VALUES(codigo || '.' || i, 0);
            END LOOP;

        --Para actualización
        ELSE
            --Si ahora hay más dependencias, se insertan las que faltan
            IF (dependencias_nuevas > dependencias_viejas) THEN
                FOR i IN (dependencias_viejas+1)..dependencias_nuevas LOOP
                    INSERT INTO sucursal VALUES(codigo || '.' || i, 0);
                END LOOP;
            
            --Si ahora hay menos dependencias, se eliminan las que faltan
            ELSE
                FOR i IN (dependencias_nuevas+1)..dependencias_viejas LOOP
                    DELETE FROM sucursal WHERE cods = (codigo || '.' || i);
                END LOOP;
            END IF;
        END IF;
    END AFTER STATEMENT;

END insercion_sucursal;
/

INSERT INTO sucursal VALUES('Azul', 4);
INSERT INTO sucursal VALUES('Pig', 0);
SELECT * FROM sucursal;

UPDATE sucursal SET nrosucdependientes = 6 WHERE cods = 'Azul';
SELECT * FROM sucursal;

UPDATE sucursal SET nrosucdependientes = 2 WHERE cods = 'Azul.3';
SELECT * FROM sucursal;

UPDATE sucursal SET nrosucdependientes = 2 WHERE cods = 'Azul.3';
SELECT * FROM sucursal;