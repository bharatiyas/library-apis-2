package com.skb.course.apis.libraryapis.service;

import com.skb.course.apis.libraryapis.LibraryApiTestUtil;
import com.skb.course.apis.libraryapis.TestConstants;
import com.skb.course.apis.libraryapis.entity.PublisherEntity;
import com.skb.course.apis.libraryapis.exception.LibraryResourceAlreadyExistException;
import com.skb.course.apis.libraryapis.exception.LibraryResourceNotFoundException;
import com.skb.course.apis.libraryapis.model.Publisher;
import com.skb.course.apis.libraryapis.repository.PublisherRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublisherServiceTest {

    @Mock
    PublisherRepository publisherRepository;

    PublisherService publisherService;

    @Before
    public void setUp() {
        publisherService = new PublisherService(publisherRepository);
    }

    @Test
    public void addPublisher_success() throws LibraryResourceAlreadyExistException {
        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(createPublisherEntity());
        Publisher publisher = publisherService.addPublisher(LibraryApiTestUtil.createPublisher(), TestConstants.API_TRACE_ID);
        assertNotNull(publisher);
        assertNotNull(publisher.getPublisherId());
        assertTrue(publisher.getName().contains(TestConstants.TEST_PUBLISHER_NAME));
        assertEquals(TestConstants.TEST_PUBLISHER_EMAIL, publisher.getEmailId());
        assertEquals(TestConstants.TEST_PUBLISHER_PHONE, publisher.getPhoneNumber());
    }

    @Test
    public void getPublisher_success() throws Exception {

        when(publisherRepository.findById(anyInt())).thenReturn(createPublisherEntityOptional());
        Publisher publisher = publisherService.getPublisher(123, TestConstants.API_TRACE_ID);

        assertNotNull(publisher);
        assertNotNull(publisher.getPublisherId());
        assertEquals(TestConstants.TEST_PUBLISHER_NAME, publisher.getName());
        assertEquals(TestConstants.TEST_PUBLISHER_EMAIL, publisher.getEmailId());
        assertEquals(TestConstants.TEST_PUBLISHER_PHONE, publisher.getPhoneNumber());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void getPublisher_failure_publisher_not_found() throws Exception {

        when(publisherRepository.findById(anyInt())).thenReturn(Optional.empty());
        publisherService.getPublisher(123, TestConstants.API_TRACE_ID);

    }

    @Test
    public void updatePublisher_success() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        PublisherEntity publisherEntity = createPublisherEntity();
        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(publisherEntity);

        Publisher publisher = publisherService.addPublisher(LibraryApiTestUtil.createPublisher(), TestConstants.API_TRACE_ID);
        assertNotNull(publisher);
        assertNotNull(publisher.getPublisherId());

        publisher.setEmailId(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED);
        publisher.setPhoneNumber(TestConstants.TEST_PUBLISHER_PHONE_UPDATED);

        when(publisherRepository.findById(anyInt())).thenReturn(createPublisherEntityOptional());

        publisher = publisherService.updatePublisher(publisher, TestConstants.API_TRACE_ID);
        assertEquals(TestConstants.TEST_PUBLISHER_NAME, publisher.getName());
        assertEquals(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED, publisher.getEmailId());
        assertEquals(TestConstants.TEST_PUBLISHER_PHONE_UPDATED, publisher.getPhoneNumber());

    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void updatePublisher_failure_publisher_not_found() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        PublisherEntity publisherEntity = createPublisherEntity();
        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(publisherEntity);

        Publisher publisher = publisherService.addPublisher(LibraryApiTestUtil.createPublisher(), TestConstants.API_TRACE_ID);
        assertNotNull(publisher);
        assertNotNull(publisher.getPublisherId());

        publisher.setEmailId(TestConstants.TEST_PUBLISHER_EMAIL_UPDATED);
        publisher.setPhoneNumber(TestConstants.TEST_PUBLISHER_PHONE_UPDATED);

        when(publisherRepository.findById(anyInt())).thenReturn(Optional.empty());
        publisherService.updatePublisher(publisher, TestConstants.API_TRACE_ID);

    }

    @Test
    public void deletePublisher_success() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(createPublisherEntity());
        Publisher publisher = publisherService.addPublisher(LibraryApiTestUtil.createPublisher(), TestConstants.API_TRACE_ID);
        assertNotNull(publisher);

        doNothing().when(publisherRepository).deleteById(anyInt());
        publisherService.deletePublisher(publisher.getPublisherId(), TestConstants.API_TRACE_ID);
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void deletePublisher_failure_publisher_not_found() throws LibraryResourceAlreadyExistException, LibraryResourceNotFoundException {

        when(publisherRepository.save(any(PublisherEntity.class))).thenReturn(createPublisherEntity());
        Publisher publisher = publisherService.addPublisher(LibraryApiTestUtil.createPublisher(), TestConstants.API_TRACE_ID);
        assertNotNull(publisher);

        doThrow(EmptyResultDataAccessException.class).when(publisherRepository).deleteById(anyInt());
        publisherService.deletePublisher(publisher.getPublisherId(), TestConstants.API_TRACE_ID);
    }

    @Test
    public void searchPublishers_success() throws LibraryResourceNotFoundException {

        List<PublisherEntity> publishersAdded = Arrays.asList(
                new PublisherEntity(TestConstants.TEST_PUBLISHER_NAME + 1, TestConstants.TEST_PUBLISHER_EMAIL, TestConstants.TEST_PUBLISHER_PHONE),
                new PublisherEntity(TestConstants.TEST_PUBLISHER_NAME + 2, TestConstants.TEST_PUBLISHER_EMAIL, TestConstants.TEST_PUBLISHER_PHONE),
                new PublisherEntity(TestConstants.TEST_PUBLISHER_NAME + 3, TestConstants.TEST_PUBLISHER_EMAIL, TestConstants.TEST_PUBLISHER_PHONE));

        when(publisherRepository.findByNameContaining(TestConstants.TEST_PUBLISHER_NAME)).thenReturn(publishersAdded);
        List<Publisher> publishersSearched = publisherService.searchPublishers(TestConstants.TEST_PUBLISHER_NAME, TestConstants.API_TRACE_ID);

        assertEquals(publishersAdded.size(), publishersSearched.size());
        assertEquals(publishersAdded.size(), publishersSearched.stream()
                .filter(publisher -> publisher.getName().contains(TestConstants.TEST_PUBLISHER_NAME))
                .count());
    }

    @Test(expected = LibraryResourceNotFoundException.class)
    public void searchPublishers_failure_publishers_not_found() throws LibraryResourceNotFoundException {

        when(publisherRepository.findByNameContaining(TestConstants.TEST_PUBLISHER_NAME)).thenReturn(Collections.emptyList());
        publisherService.searchPublishers(TestConstants.TEST_PUBLISHER_NAME, TestConstants.API_TRACE_ID);

    }

    private PublisherEntity createPublisherEntity() {
        return new PublisherEntity(TestConstants.TEST_PUBLISHER_NAME, TestConstants.TEST_PUBLISHER_EMAIL, TestConstants.TEST_PUBLISHER_PHONE);
    }

    private Optional<PublisherEntity> createPublisherEntityOptional() {
        return Optional.of(createPublisherEntity());
    }
}