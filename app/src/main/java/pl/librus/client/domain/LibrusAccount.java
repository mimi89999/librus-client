package pl.librus.client.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.immutables.value.Value;

import javax.persistence.Embeddable;

import io.requery.Key;

@Embeddable
@Value.Immutable
@JsonDeserialize(as = ImmutableLibrusAccount.class)
public abstract class LibrusAccount {

    @Key
    public abstract String login();

    public abstract String firstName();

    public abstract String lastName();

    public String name() {
        return firstName() + " " + lastName();
    }

    public static ImmutableLibrusAccount.Builder builder()  {
        return ImmutableLibrusAccount.builder();
    }

}
