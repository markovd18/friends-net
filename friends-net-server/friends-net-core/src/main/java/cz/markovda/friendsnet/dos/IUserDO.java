package cz.markovda.friendsnet.dos;

public interface IUserDO {

    int getId();

    String getLogin();

    String getPassword();

    EnumUserRole getRole();

    enum EnumUserRole {
        ADMIN,
        USER
    }
}