package de.nowakhub.miniwelt.model;

import java.util.*;

public enum Field {    
    FREE(""),
    OBSTACLE("O"),
    ITEM("I"),
    ACTOR("A"),
    START("S"),
    ACTOR_AT_START("AS"),
    ACTOR_ON_ITEM("AI");

    private String key;

    Field(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }

    public boolean isOccupiedBy(Field by) {
        switch (by) {
            case ACTOR:
                return Field.WithActor.contains(this);
            case START:
                return Field.WithStart.contains(this);
            case ITEM:
                return Field.WithActor.contains(this);
            default:
                return this.equals(by);
        }
    }
    
    public Field without(Field without) {
        if (this.equals(without)) return FREE;
        if (StackedOnActor.contains(this)) {
            if (ACTOR.equals(without)) {
                return byKey(this.getKey().substring(1));
            } else if (StackableOnActor.contains(without)) {
                return byKey(this.getKey().substring(0, 1));
            }
        }
        return this;
    }


    public Field with(Field with) {
        if (ACTOR.equals(with) && StackableOnActor.contains(this)) {
            return byKey(with.getKey().concat(this.getKey()));
        } else if (ACTOR.equals(this) && StackableOnActor.contains(with)) {
            return byKey(this.getKey().concat(with.getKey()));
        }
        return with;
    }

    public Field clear() {
        if (this.equals(FREE)) return FREE;
        return byKey(this.getKey().substring(0, this.getKey().length() - 1));
    }

    public static EnumSet<Field> NotRemovable = EnumSet.of(Field.ACTOR, Field.ACTOR_AT_START, Field.ACTOR_ON_ITEM, Field.START, Field.ACTOR_AT_START);
    public static EnumSet<Field> StackableOnActor = EnumSet.of(Field.START, Field.ITEM);
    public static EnumSet<Field> StackedOnActor = EnumSet.of(Field.ACTOR_AT_START, Field.ACTOR_ON_ITEM);
    public static EnumSet<Field> WithActor = EnumSet.of(Field.ACTOR, Field.ACTOR_AT_START, Field.ACTOR_ON_ITEM);
    public static EnumSet<Field> WithStart = EnumSet.of(Field.START, Field.ACTOR_AT_START);
    public static EnumSet<Field> WithItems = EnumSet.of(Field.ITEM, Field.ACTOR_ON_ITEM);
    public static final Map<String, Field> lookup;
    
    static {
        lookup = Collections.unmodifiableMap(
                Arrays.stream(values())
                        .collect(HashMap::new, (m, f) -> m.put(f.getKey(), f), Map::putAll)
        );
    }

    public static Field byKey(String key) {
        return lookup.get(key);
    }
}
