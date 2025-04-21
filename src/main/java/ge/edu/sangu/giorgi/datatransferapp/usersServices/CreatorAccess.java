package ge.edu.sangu.giorgi.datatransferapp.usersServices;

public class CreatorAccess {

    /**
     *
     * @return TRUE if user uses guest account or email is null
     */
    public static boolean checkAccess(){
        return AuthData.getEMAIL() == null || AuthData.getEMAIL().equals("guestemail@email.com");
    }
}
