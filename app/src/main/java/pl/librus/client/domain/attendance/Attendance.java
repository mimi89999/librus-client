package pl.librus.client.domain.attendance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import io.requery.Entity;
import pl.librus.client.domain.Identifiable;

/**
 * Created by Adam on 13.12.2016.
 * Class representing /Attendances item
 */

@Entity(builder = ImmutableAttendance.Builder.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableAttendance.class)
public abstract class Attendance extends BaseAttendance implements Identifiable {

}
