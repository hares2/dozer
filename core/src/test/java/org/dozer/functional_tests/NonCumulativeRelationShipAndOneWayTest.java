package org.dozer.functional_tests;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lampinex
 *
 * The purpose of this test is to verify non-regression of a bug in dozer :
 *
 * This one occurs when we maps a graph containing a set with no-cumulative-relationship and some one-way properties to another graph
 * containing also a set. Therefore all sub-elements in the elements set are build from scratch and mapped from the source, and in the case
 * where some properties of these elements are one-way, we lose those informations !
 */
public class NonCumulativeRelationShipAndOneWayTest extends AbstractFunctionalTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonCumulativeRelationShipAndOneWayTest.class);

    private Mapper mapper;

    @Override
    @Before
    public void setUp() throws Exception {
        this.mapper = getMapper("NonCumulativeRelationShipAndOneWayTest.xml");
    }

    @Test
    public void testNonCumulativeRelationShipAndOneWayMapping() {

        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setCreatedDate(new Date());
        simpleBean.setModifiedDate(new Date());

        SimpleBeanDTO simpleBeanDTO = this.mapper.map(simpleBean, SimpleBeanDTO.class);

        ListElement listElement = new ListElement();
        listElement.setCreatedDate(new Date());
        listElement.setModifiedDate(new Date());
        listElement.setSimpleBean(simpleBean);

        ListElementDTO listElementDTO = new ListElementDTO();
        listElementDTO.setSimpleBean(simpleBeanDTO);
        listElementDTO.setId(listElement.getId());

        ListContainer listContainer = new ListContainer();
        listContainer.addAbstractListElement(listElement);

        ListContainerDTO listContainerDTO = new ListContainerDTO();
        listContainerDTO.setId(listContainer.getId());
        listContainerDTO.addListElement(listElementDTO);

        this.mapper.map(listContainerDTO, listContainer);

        Assert.assertNotNull("ListElement::SimpleBean::CreatedDate must not be null", listElement.getSimpleBean().getCreatedDate());
        Assert.assertTrue("ListElement::SimpleBean::CreatedDate and SimpleBeanDTO::CreatedDate must be equal to its DTO's counter part", listElement.getSimpleBean().getCreatedDate().equals(listElementDTO.getSimpleBean().getCreatedDate()));
        Assert.assertEquals(1, listContainer.getMyList().size());
    }


    public static abstract class IdentifiedObject {

        protected String id;

        protected Date createdDate;

        protected Date modifiedDate;

        protected Date obsoleteDate;

        public IdentifiedObject() {
            id = String.valueOf(System.nanoTime());
        }

        public IdentifiedObject(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        /**
         * @param id
         */
        public void setId(String id) {
            this.id = id;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         */
        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
        }

        public Date getModifiedDate() {
            return modifiedDate;
        }

        /**
         * @param modifiedDate
         */
        public void setModifiedDate(Date modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public Date getObsoleteDate() {
            return obsoleteDate;
        }

        public void setObsoleteDate(final Date obsoleteDate) {
            this.obsoleteDate = obsoleteDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IdentifiedObject)) return false;

            IdentifiedObject that = (IdentifiedObject) o;

            return !(id != null ? !id.equals(that.id) : that.id != null);

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    public static class SimpleBean extends IdentifiedObject {

        public SimpleBean() {

        }
    }

    public static abstract class AbstractListElement extends IdentifiedObject {

        protected SimpleBean simpleBean;

        protected AbstractListElement() {
        }


        public SimpleBean getSimpleBean() {
            return simpleBean;
        }

        public void setSimpleBean(SimpleBean simpleBean) {
            this.simpleBean = simpleBean;
        }

    }

    public static class ListElement extends AbstractListElement {

        public ListElement() {
        }
    }

    public static class ListContainer extends IdentifiedObject {

        private Set<AbstractListElement> myList;

        public ListContainer() {
        }

        public ListContainer(final String id) {
            super(id);
        }

        public void addAbstractListElement(final AbstractListElement abstractListElement) {
            if (myList == null) {
                myList = new HashSet<AbstractListElement>();
            }
            myList.add(abstractListElement);
        }

        public void removeAbstractListElement(final AbstractListElement abstractListElement) {
            if (myList != null && myList.contains(abstractListElement)) {
                myList.remove(abstractListElement);
            }
        }

        public Set<AbstractListElement> getMyList() {
            if (myList == null)
                myList = new HashSet<AbstractListElement>();
            return myList;
        }

        public void setMyList(Set<AbstractListElement> myList) {
            this.myList = myList;
        }

    }

    /**
     * DTO's
     */

    public static abstract class IdentifiedDTO {

        protected String id;
        protected Date createdDate;
        protected Date modifiedDate;
        protected Date obsoleteDate;

        public Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(final Date createdDate) {
            this.createdDate = createdDate;
        }

        public Date getModifiedDate() {
            return modifiedDate;
        }

        public void setModifiedDate(final Date modifiedDate) {
            this.modifiedDate = modifiedDate;
        }

        public Date getObsoleteDate() {
            return obsoleteDate;
        }

        public void setObsoleteDate(Date obsoleteDate) {
            this.obsoleteDate = obsoleteDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IdentifiedDTO that = (IdentifiedDTO) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

    public static class SimpleBeanDTO extends IdentifiedDTO {

        public SimpleBeanDTO() {
            super();
        }
    }

    public static abstract class AbstractListElementDTO extends IdentifiedDTO {

        public SimpleBeanDTO getSimpleBean() {
            return simpleBean;
        }

        public void setSimpleBean(SimpleBeanDTO simpleBean) {
            this.simpleBean = simpleBean;
        }

        protected SimpleBeanDTO simpleBean;
    }

    public static class ListElementDTO extends AbstractListElementDTO {

        public SimpleBeanDTO getSecondSimpleBean() {
            return secondSimpleBean;
        }

        public void setSecondSimpleBean(SimpleBeanDTO secondSimpleBean) {
            this.secondSimpleBean = secondSimpleBean;
        }

        private SimpleBeanDTO secondSimpleBean;

        public ListElementDTO() {
        }

    }

    public static class ListContainerDTO extends IdentifiedDTO {

        private Set<AbstractListElementDTO> myList;


        public Set<AbstractListElementDTO> getMyList() {
            return myList;
        }

        public void setMyList(final Set<AbstractListElementDTO> myList) {
            this.myList = myList;
        }


        public void addListElement(final AbstractListElementDTO abstractListElementDTO) {
            if (myList == null) {
                myList = new HashSet<AbstractListElementDTO>();
            }
            myList.add(abstractListElementDTO);
        }

        public void removeListElement(final AbstractListElementDTO abstractListElementDTO) {
            if (myList != null && myList.contains(abstractListElementDTO)) {
                myList.remove(abstractListElementDTO);
            }
        }

    }

}
