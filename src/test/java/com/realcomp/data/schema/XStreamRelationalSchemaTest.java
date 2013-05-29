package com.realcomp.data.schema;

import org.junit.Before;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class XStreamRelationalSchemaTest{

    private XStream xstream;

    @Before
    public void init(){

        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(RelationalSchema.class);
        xstream.processAnnotations(Table.class);
    }

    protected RelationalSchema getSchema() throws SchemaException{
        RelationalSchema schema = new RelationalSchema();
        schema.setName("test");
        schema.setVersion("1.0");


        Table prop = new Table("prop");
        // prop.addKey(new KeyField("prop_id"));

        Table impInfo = new Table("imp_info");
        // impInfo.addKey(new ForeignKeyField("prop_id"));
        // impInfo.addKey(new KeyField("impvr_id"));

        Table impDet = new Table("imp_det");
        // impDet.addKey(new ForeignKeyField("prop_id"));
        // impDet.addKey(new ForeignKeyField("imprv_id"));
        // impDet.addKey(new KeyField("imprv_det_id"));

        Table impAtr = new Table("imp_atr");
        // impAtr.addKey(new ForeignKeyField("prop_id"));
        // impAtr.addKey(new ForeignKeyField("imprv_id"));
        // impAtr.addKey(new ForeignKeyField("imprv_det_id"));
        // impAtr.addKey(new KeyField("imprv_attr_id"));

        impDet.add(impAtr);
        impInfo.add(impDet);

        Table landDet = new Table("land_det");
        // landDet.addKey(new ForeignKeyField("prop_id"));
        // landDet.addKey(new KeyField("land_seg_id"));

        Table propEnt = new Table("prop_ent");
        // propEnt.addKey(new ForeignKeyField("prop_id"));
        // propEnt.addKey(new KeyField("prop_val_yr"));
        // propEnt.addKey(new KeyField("entity_id"));

        prop.add(impInfo);
        prop.add(landDet);
        prop.add(propEnt);
        schema.addTable(prop);

        return schema;
    }

    @Test
    public void testTableEquality() throws SchemaException{

        Table a = new Table("test");
        Table b = new Table("test");
        assertEquals(a, b);

        Table c = new Table("child");
        Table d = new Table("child");
        a.add(c);
        b.add(d);
        assertEquals(a, b);
    }

    @Test
    public void testSchemeaEquality() throws SchemaException{

        RelationalSchema a = getSchema();
        RelationalSchema b = getSchema();
        assertEquals(a.getTables(), b.getTables());
    }

    @Test
    public void testSerialization() throws SchemaException{

        String xml = xstream.toXML(getSchema());
        RelationalSchema deserialized = (RelationalSchema) xstream.fromXML(xml);
        assertEquals(1, deserialized.getTables().size());

        Table prop = deserialized.getTables().iterator().next();
        assertNotNull(prop);
        assertEquals(prop, prop.getChildren().iterator().next().getParent());
        assertEquals(3, prop.getChildren().size());
        assertTrue(getSchema().equals(deserialized));
    }
}
