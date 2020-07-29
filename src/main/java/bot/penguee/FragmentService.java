package bot.penguee;

import bot.penguee.exception.FragmentDuplicateException;
import bot.penguee.exception.FragmentNotLoadedException;
import bot.penguee.fragments.Frag;

import java.util.HashMap;

public class FragmentService {
    private HashMap<String, Frag> fragments = new HashMap<String, Frag>();
    public FragmentService(){

    }

    public void put(Frag f, String name) throws FragmentDuplicateException {
        if (fragments.containsKey(name)){
            throw new FragmentDuplicateException(name);
        }else{
            fragments.put(name, f);
        }
    }
}
