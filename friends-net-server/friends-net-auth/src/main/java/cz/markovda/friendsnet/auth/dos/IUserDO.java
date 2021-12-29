package cz.markovda.friendsnet.auth.dos;

public interface IUserDO {

    int getId();

    String getLogin();

    String getPassword();

    String getName();

    EnumUserRole getRole();

    enum EnumUserRole {
        ADMIN,
        USER
    }
}