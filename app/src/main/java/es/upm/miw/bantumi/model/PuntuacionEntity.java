package es.upm.miw.bantumi.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDateTime;
import java.util.Date;

import es.upm.miw.bantumi.util.Converters;

@Entity(tableName = PuntuacionEntity.TABLA)
@TypeConverters({Converters.class})
public class PuntuacionEntity {
    final static String TABLA = "puntuaciones";

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String username;

    private Date date;

    private Integer playerSeeds;

    private Integer opponentSeeds;

    private Long gameDurationSecs;

    public PuntuacionEntity() {
    }

    public PuntuacionEntity(@NonNull String username, Date date, Integer playerSeeds, Integer opponentSeeds, Long gameDurationSecs) {
        this.username = username;
        this.date = date;
        this.playerSeeds = playerSeeds;
        this.opponentSeeds = opponentSeeds;
        this.gameDurationSecs = gameDurationSecs;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getPlayerSeeds() {
        return playerSeeds;
    }

    public void setPlayerSeeds(Integer playerSeeds) {
        this.playerSeeds = playerSeeds;
    }

    public Integer getOpponentSeeds() {
        return opponentSeeds;
    }

    public void setOpponentSeeds(Integer opponentSeeds) {
        this.opponentSeeds = opponentSeeds;
    }

    public Long getGameDurationSecs() {
        return gameDurationSecs;
    }

    public void setGameDurationSecs(Long gameDurationSecs) {
        this.gameDurationSecs = gameDurationSecs;
    }
}
