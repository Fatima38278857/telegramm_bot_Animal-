package com.example.ShelterBot.bot.model;

import com.example.ShelterBot.bot.enumm.StatusDog;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "dog")
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "age")
    private int age;
    @Enumerated(EnumType.STRING)
    @Column(name = "statusDog")
    private StatusDog statusDog;
    @OneToOne
    @JoinColumn(name = "id_user")
    private User user;

    public Dog(Long id, String nickname, int age, StatusDog statusDog, User user) {
        this.id = id;
        this.nickname = nickname;
        this.age = age;
        this.statusDog = statusDog;
        this.user = user;
    }

    public Dog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public StatusDog getStatusDog() {
        return statusDog;
    }

    public void setStatusDog(StatusDog statusDog) {
        this.statusDog = statusDog;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        return age == dog.age && Objects.equals(id, dog.id) && Objects.equals(nickname, dog.nickname) && statusDog == dog.statusDog && Objects.equals(user, dog.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, age, statusDog, user);
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                ", statusDog=" + statusDog +
                ", user=" + user +
                '}';
    }
}

