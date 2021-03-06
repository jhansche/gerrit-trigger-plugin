package com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data;

import com.sonyericsson.hudson.plugins.gerrit.gerritevents.dto.events.PatchsetCreated;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritCause;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.actions.RetriggerAction;
import com.sonyericsson.hudson.plugins.gerrit.trigger.mock.Setup;
import com.thoughtworks.xstream.XStream;
import hudson.matrix.MatrixRun;
import hudson.model.Cause;
import hudson.util.XStream2;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.TriggerContextConverter}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class TriggerContextConverterTest {

    //CS IGNORE MagicNumber FOR NEXT 300 LINES. REASON: test data.
    //CS IGNORE LineLength FOR NEXT 4 LINES. REASON: Javadoc.

    /**
     * Tests {@link TriggerContextConverter#marshal(Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)}. With an empty list of "others".
     *
     * @throws Exception if so.
     */
    @Test
    public void testMarshalNoOthers() throws Exception {
        TriggeredItemEntity entity = new TriggeredItemEntity(100, "projectX");

        PatchsetCreated event = Setup.createPatchsetCreated();
        TriggerContext context = new TriggerContext(event);
        context.setThisBuild(entity);
        context.setOthers(new LinkedList<TriggeredItemEntity>());

        TestMarshalClass t = new TestMarshalClass(context, "Bobby", new TestMarshalClass(context, "SomeoneElse"));

        XStream xStream = new XStream2();
        xStream.registerConverter(new TriggerContextConverter());
        String xml = xStream.toXML(t);

        TestMarshalClass readT = (TestMarshalClass)xStream.fromXML(xml);

        assertNotNull(readT.getEntity());
        assertNotNull(readT.getEntity().getEvent());
        assertNotNull(readT.getEntity().getThisBuild());

        assertEquals("project", readT.getEntity().getEvent().getChange().getProject());
        assertEquals(100, readT.getEntity().getThisBuild().getBuildNumber().intValue());
        assertEquals("projectX", readT.getEntity().getThisBuild().getProjectId());

        assertSame(readT.getEntity(), readT.getTestClass().getEntity());
    }

    /**
     * Tests {@link TriggerContextConverter#marshal(Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)}. With {@link TriggerContext#thisBuild} set to null.
     *
     * @throws Exception if so.
     */
    @Test
    public void testMarshalNoThisBuild() throws Exception {
        PatchsetCreated event = Setup.createPatchsetCreated();
        TriggerContext context = new TriggerContext(event);
        context.setOthers(new LinkedList<TriggeredItemEntity>());

        TestMarshalClass t = new TestMarshalClass(context, "Me", new TestMarshalClass(context, "SomeoneElse"));

        XStream xStream = null;
        String xml = null;
        try {
            xStream = new XStream2();
            xStream.registerConverter(new TriggerContextConverter());
            xml = xStream.toXML(t);
        } catch (Exception e) {
            AssertionError error = new AssertionError("This should work, but did not. " + e.getMessage());
            error.initCause(e);
            throw error;
        }

        TestMarshalClass readT = (TestMarshalClass)xStream.fromXML(xml);

        assertNotNull(readT.getEntity());
        assertNotNull(readT.getEntity().getEvent());
    }

    /**
     * Tests {@link TriggerContextConverter#marshal(Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)}. With {@link TriggerContext#event} set to null.
     *
     * @throws Exception if so.
     */
    @Test
    public void testMarshalNoEvent() throws Exception {
        TriggeredItemEntity entity = new TriggeredItemEntity(100, "projectX");
        TriggerContext context = new TriggerContext(null);
        context.setThisBuild(entity);
        context.setOthers(new LinkedList<TriggeredItemEntity>());

        TestMarshalClass t = new TestMarshalClass(context, "Me", new TestMarshalClass(context, "SomeoneElse"));

        XStream xStream = null;
        String xml = null;
        try {
            xStream = new XStream2();
            xStream.registerConverter(new TriggerContextConverter());
            xml = xStream.toXML(t);
        } catch (Exception e) {
            AssertionError error = new AssertionError("This should work, but did not. " + e.getMessage());
            error.initCause(e);
            throw error;
        }

        TestMarshalClass readT = (TestMarshalClass)xStream.fromXML(xml);

        assertNotNull(readT.getEntity());
        assertNull(readT.getEntity().getEvent());

        assertNotNull(readT.getEntity().getThisBuild());

        assertEquals(100, readT.getEntity().getThisBuild().getBuildNumber().intValue());
        assertEquals("projectX", readT.getEntity().getThisBuild().getProjectId());

        assertSame(readT.getEntity(), readT.getTestClass().getEntity());
    }

    //CS IGNORE LineLength FOR NEXT 4 LINES. REASON: Javadoc.

    /**
     * Tests {@link TriggerContextConverter#marshal(Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)}. With list of "others" containing two items.
     *
     * @throws Exception if so.
     */
    @Test
    public void testMarshalWithOthers() throws Exception {
        TriggeredItemEntity entity = new TriggeredItemEntity(100, "projectX");

        PatchsetCreated event = Setup.createPatchsetCreated();
        TriggerContext context = new TriggerContext(event);
        context.setThisBuild(entity);
        LinkedList<TriggeredItemEntity> otherBuilds = new LinkedList<TriggeredItemEntity>();
        otherBuilds.add(new TriggeredItemEntity(1, "projectY"));
        otherBuilds.add(new TriggeredItemEntity(12, "projectZ"));
        context.setOthers(otherBuilds);

        TestMarshalClass t = new TestMarshalClass(context, "Bobby", new TestMarshalClass(context, "SomeoneElse"));

        XStream xStream = new XStream2();
        xStream.registerConverter(new TriggerContextConverter());
        String xml = xStream.toXML(t);

        TestMarshalClass readT = (TestMarshalClass)xStream.fromXML(xml);

        assertNotNull(readT.getEntity());
        assertNotNull(readT.getEntity().getEvent());
        assertNotNull(readT.getEntity().getThisBuild());
        assertNotNull(readT.getEntity().getOthers());

        assertEquals(2, readT.getEntity().getOthers().size());

        TriggeredItemEntity other = readT.getEntity().getOthers().get(0);
        assertEquals(1, other.getBuildNumber().intValue());
        assertEquals("projectY", other.getProjectId());

        other = readT.getEntity().getOthers().get(1);
        assertEquals(12, other.getBuildNumber().intValue());
        assertEquals("projectZ", other.getProjectId());
    }

    /**
     * Tests {@link TriggerContextConverter#marshal(Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter,
     * com.thoughtworks.xstream.converters.MarshallingContext)}. With list of "others" containing two items and a null
     * item between them.
     *
     * @throws Exception if so.
     */
    @Test
    public void testMarshalWithOthersOneNull() throws Exception {
        TriggeredItemEntity entity = new TriggeredItemEntity(100, "projectX");

        PatchsetCreated event = Setup.createPatchsetCreated();
        TriggerContext context = new TriggerContext(event);
        context.setThisBuild(entity);
        LinkedList<TriggeredItemEntity> otherBuilds = new LinkedList<TriggeredItemEntity>();
        otherBuilds.add(new TriggeredItemEntity(1, "projectY"));
        otherBuilds.add(null);
        otherBuilds.add(new TriggeredItemEntity(12, "projectZ"));
        context.setOthers(otherBuilds);

        TestMarshalClass t = new TestMarshalClass(context, "Bobby", new TestMarshalClass(context, "SomeoneElse"));

        XStream xStream = null;
        String xml = null;
        try {
            xStream = new XStream2();
            xStream.registerConverter(new TriggerContextConverter());
            xml = xStream.toXML(t);
        } catch (Exception e) {
            AssertionError error = new AssertionError("This should work, but did not. " + e.getMessage());
            error.initCause(e);
            throw error;
        }

        TestMarshalClass readT = (TestMarshalClass)xStream.fromXML(xml);

        assertNotNull(readT.getEntity());
        assertNotNull(readT.getEntity().getEvent());
        assertNotNull(readT.getEntity().getThisBuild());
        assertNotNull(readT.getEntity().getOthers());

        assertEquals(2, readT.getEntity().getOthers().size());

        TriggeredItemEntity other = readT.getEntity().getOthers().get(0);
        assertEquals(1, other.getBuildNumber().intValue());
        assertEquals("projectY", other.getProjectId());

        other = readT.getEntity().getOthers().get(1);
        assertEquals(12, other.getBuildNumber().intValue());
        assertEquals("projectZ", other.getProjectId());
    }

    //CS IGNORE LineLength FOR NEXT 4 LINES. REASON: Javadoc.

    /**
     * Tests {@link TriggerContextConverter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)}. With "retriggerAction_oldData.xml" as input.
     *
     * @throws Exception if so.
     */
    @Test
    public void testUnmarshalOldData1() throws Exception {
        XStream xStream = new XStream2();
        xStream.registerConverter(new TriggerContextConverter());
        Object obj = xStream.fromXML(getClass().getResourceAsStream("retriggerAction_oldData.xml"));
        assertTrue(obj instanceof RetriggerAction);
        RetriggerAction action = (RetriggerAction)obj;
        TriggerContext context = Whitebox.getInternalState(action, "context");
        assertNotNull(context.getEvent());
        assertEquals("semctools/hudson/plugins/gerrit-trigger-plugin", context.getEvent().getChange().getProject());
        assertEquals("1", context.getEvent().getPatchSet().getNumber());

        assertNotNull(context.getThisBuild());
        assertEquals(6, context.getThisBuild().getBuildNumber().intValue());
        assertEquals("EXPERIMENTAL_Gerrit_Trigger_1", context.getThisBuild().getProjectId());

        assertNotNull(context.getOthers());
        assertEquals(1, context.getOthers().size());
        TriggeredItemEntity entity = context.getOthers().get(0);
        assertEquals(16, entity.getBuildNumber().intValue());
        assertEquals("EXPERIMENTAL_Gerrit_Trigger_2", entity.getProjectId());
    }

    //CS IGNORE LineLength FOR NEXT 4 LINES. REASON: Javadoc.

    /**
     * Tests {@link TriggerContextConverter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)}. With "retriggerAction_oldData2.xml" as input.
     *
     * @throws Exception if so.
     */
    @Test
    public void testUnmarshalOldData2() throws Exception {
        XStream xStream = new XStream2();
        xStream.registerConverter(new TriggerContextConverter());
        Object obj = xStream.fromXML(getClass().getResourceAsStream("retriggerAction_oldData2.xml"));
        assertTrue(obj instanceof RetriggerAction);
        RetriggerAction action = (RetriggerAction)obj;
        TriggerContext context = Whitebox.getInternalState(action, "context");
        assertNotNull(context.getEvent());
        assertEquals("semctools/hudson/plugins/gerrit-trigger-plugin", context.getEvent().getChange().getProject());
        assertEquals("1", context.getEvent().getPatchSet().getNumber());

        assertNotNull(context.getThisBuild());
        assertEquals(6, context.getThisBuild().getBuildNumber().intValue());
        assertEquals("EXPERIMENTAL_Gerrit_Trigger_1", context.getThisBuild().getProjectId());

        assertNotNull(context.getOthers());
        assertEquals(2, context.getOthers().size());
        TriggeredItemEntity entity = context.getOthers().get(0);
        assertEquals(16, entity.getBuildNumber().intValue());
        assertEquals("EXPERIMENTAL_Gerrit_Trigger_2", entity.getProjectId());
        entity = context.getOthers().get(1);
        assertEquals(15, entity.getBuildNumber().intValue());
        assertEquals("EXPERIMENTAL_Gerrit_Trigger_3", entity.getProjectId());
    }

    //CS IGNORE LineLength FOR NEXT 4 LINES. REASON: Javadoc.

    /**
     * Tests {@link TriggerContextConverter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)}. With "matrix_build.xml" as input.
     *
     * @throws Exception if so.
     */
    @Test
    public void testUnmarshalOldMatrixBuild() throws Exception {
        XStream xStream = new XStream2();
        xStream.registerConverter(new TriggerContextConverter());
        xStream.alias("matrix-run", MatrixRun.class);
        Object obj = xStream.fromXML(getClass().getResourceAsStream("matrix_build.xml"));
        assertTrue(obj instanceof MatrixRun);
        MatrixRun run = (MatrixRun)obj;

        Cause.UpstreamCause upCause = run.getCause(Cause.UpstreamCause.class);
        List upstreamCauses = Whitebox.getInternalState(upCause, "upstreamCauses");
        GerritCause cause = (GerritCause)upstreamCauses.get(0);
        assertNotNull(cause.getEvent());
        assertEquals("platform/project", cause.getEvent().getChange().getProject());
        assertNotNull(cause.getContext());
        assertNotNull(cause.getContext().getThisBuild());

        assertEquals("Gerrit_master-theme_matrix", cause.getContext().getThisBuild().getProjectId());
        assertEquals(102, cause.getContext().getThisBuild().getBuildNumber().intValue());

        assertNotNull(cause.getContext().getOthers());
        assertEquals(1, cause.getContext().getOthers().size());

        TriggeredItemEntity entity = cause.getContext().getOthers().get(0);
        assertEquals("master-theme", entity.getProjectId());
        assertNull(entity.getBuildNumber());
    }

    /**
     * Tests {@link TriggerContextConverter#canConvert(Class)}. With {@link TriggerContext}.class as input.
     *
     * @throws Exception if so.
     */
    @Test
    public void testCanConvert() throws Exception {
        TriggerContextConverter conv = new TriggerContextConverter();
        assertTrue(conv.canConvert(TriggerContext.class));
    }

    /**
     * Tests {@link TriggerContextConverter#canConvert(Class)}. With {@link String}.class as input.
     *
     * @throws Exception if so.
     */
    @Test
    public void testCanConvertString() throws Exception {
        TriggerContextConverter conv = new TriggerContextConverter();
        assertFalse(conv.canConvert(String.class));
    }

    /**
     * Tests {@link TriggerContextConverter#canConvert(Class)}. With a subclass of {@link TriggerContext} as input.
     *
     * @throws Exception if so.
     */
    @Test
    public void testCanConvertSub() throws Exception {
        TriggerContextConverter conv = new TriggerContextConverter();
        assertFalse(conv.canConvert(TriggerContextSub.class));
    }

    /**
     * A qnd subclass of {@link com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.data.TriggerContext}. As
     * input to the {@link #testCanConvertSub} test.
     */
    static class TriggerContextSub extends TriggerContext {

    }


    /**
     * A simple POJO class that contains a {@link TriggerContext}. To aid in the marshal testing.
     */
    static class TestMarshalClass {
        private TriggerContext entity;
        private String name;
        private TestMarshalClass testClass;

        /**
         * Default Constructor.
         */
        @SuppressWarnings("unused")
        //called by XStream
        TestMarshalClass() {
        }

        /**
         * Standard constructor.
         *
         * @param entity a context.
         * @param name   a string.
         */
        TestMarshalClass(TriggerContext entity, String name) {
            this.entity = entity;
            this.name = name;
        }

        /**
         * Standard constructor.
         *
         * @param entity    a context.
         * @param name      a string.
         * @param testClass a second level.
         */
        TestMarshalClass(TriggerContext entity, String name, TestMarshalClass testClass) {
            this.entity = entity;
            this.name = name;
            this.testClass = testClass;
        }

        /**
         * Get the context.
         *
         * @return the context.
         */
        public TriggerContext getEntity() {
            return entity;
        }

        /**
         * The name.
         *
         * @return the name.
         */
        public String getName() {
            return name;
        }

        /**
         * The second level.
         *
         * @return the second level.
         */
        public TestMarshalClass getTestClass() {
            return testClass;
        }
    }
}
