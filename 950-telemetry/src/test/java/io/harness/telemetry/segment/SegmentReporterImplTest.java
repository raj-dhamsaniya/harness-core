package io.harness.telemetry.segment;

import static io.harness.rule.OwnerRule.ZHUO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import io.harness.annotations.dev.HarnessTeam;
import io.harness.annotations.dev.OwnedBy;
import io.harness.category.element.UnitTests;
import io.harness.rule.Owner;
import io.harness.security.SecurityContextBuilder;
import io.harness.security.dto.UserPrincipal;
import io.harness.telemetry.Destination;
import io.harness.telemetry.TelemetrySdkTestBase;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.segment.analytics.messages.GroupMessage;
import com.segment.analytics.messages.IdentifyMessage;
import com.segment.analytics.messages.TrackMessage;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@OwnedBy(HarnessTeam.GTM)
@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextBuilder.class)
public class SegmentReporterImplTest extends TelemetrySdkTestBase {
  @Mock SegmentSender segmentSender;
  @Inject @InjectMocks SegmentReporterImpl segmentReporterImpl;

  private static final String EMAIL = "dummy@dummy";
  private static final String DESTINATION = "Natero";
  private static final String USER_ID_KEY = "userId";
  private static final String GROUP_ID_KEY = "groupId";
  private static final String ACCOUNT_ID = "123";
  private static final String PROPERTY_KEY = "a";
  private static final String PROPERTY_VALUE = "b";
  private HashMap<String, Object> properties;
  private Map<Destination, Boolean> destinations;
  private ArgumentCaptor<TrackMessage.Builder> trackCaptor;
  private ArgumentCaptor<GroupMessage.Builder> groupCaptor;
  private ArgumentCaptor<IdentifyMessage.Builder> identifyCaptor;

  @Before
  public void setUp() {
    initMocks(this);
    PowerMockito.mockStatic(SecurityContextBuilder.class);
    Mockito.when(SecurityContextBuilder.getPrincipal())
        .thenReturn(new UserPrincipal("dummy", EMAIL, "dummy", ACCOUNT_ID));
    properties = new HashMap<>();
    properties.put(PROPERTY_KEY, PROPERTY_VALUE);
    destinations = ImmutableMap.<Destination, Boolean>builder().put(Destination.NATERO, true).build();
    trackCaptor = ArgumentCaptor.forClass(TrackMessage.Builder.class);
    groupCaptor = ArgumentCaptor.forClass(GroupMessage.Builder.class);
    identifyCaptor = ArgumentCaptor.forClass(IdentifyMessage.Builder.class);
  }

  @Test
  @Owner(developers = ZHUO)
  @Category(UnitTests.class)
  public void testSendTrackEvent() {
    Mockito.when(segmentSender.isEnabled()).thenReturn(true);
    segmentReporterImpl.sendTrackEvent("test", properties, destinations);
    Mockito.verify(segmentSender).enqueue(trackCaptor.capture());
    TrackMessage message = trackCaptor.getValue().build();
    assertThat(message.event()).isEqualTo("test");
    assertThat(message.properties().get(PROPERTY_KEY)).isEqualTo(PROPERTY_VALUE);
    assertThat(message.properties().get(GROUP_ID_KEY)).isEqualTo(ACCOUNT_ID);
    assertThat(message.properties().get(USER_ID_KEY)).isEqualTo(EMAIL);
    assertThat(message.userId()).isEqualTo(EMAIL);
    assertThat(message.integrations().get(DESTINATION)).isEqualTo(true);
  }

  @Test
  @Owner(developers = ZHUO)
  @Category(UnitTests.class)
  public void testSendGroupEvent() {
    Mockito.when(segmentSender.isEnabled()).thenReturn(true);
    segmentReporterImpl.sendGroupEvent("accountId", properties, destinations);
    Mockito.verify(segmentSender).enqueue(groupCaptor.capture());
    GroupMessage message = groupCaptor.getValue().build();
    assertThat(message.groupId()).isEqualTo("accountId");
    assertThat(message.traits().get(PROPERTY_KEY)).isEqualTo(PROPERTY_VALUE);
    assertThat(message.userId()).isEqualTo(EMAIL);
    assertThat(message.integrations().get(DESTINATION)).isEqualTo(true);
  }

  @Test
  @Owner(developers = ZHUO)
  @Category(UnitTests.class)
  public void testSendIdentifyEent() {
    Mockito.when(segmentSender.isEnabled()).thenReturn(true);
    segmentReporterImpl.sendIdentifyEvent("user", properties, destinations);
    Mockito.verify(segmentSender).enqueue(identifyCaptor.capture());
    IdentifyMessage message = identifyCaptor.getValue().build();
    assertThat(message.userId()).isEqualTo("user");
    assertThat(message.traits().get(PROPERTY_KEY)).isEqualTo(PROPERTY_VALUE);
    assertThat(message.integrations().get(DESTINATION)).isEqualTo(true);
  }
}