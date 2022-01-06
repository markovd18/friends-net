package cz.markovda.friendsnet.auth.dos;

public interface IUserDO {

    Integer getId();

    String getLogin();

    String getPassword();

    String getName();

    enum EnumUserRole {
        ADMIN,
        USER
    }
}