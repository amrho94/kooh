package cc.prism.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {

    private final Map<Class<?>, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventTarget.class) && method.getParameterCount() == 1) {
                Class<?> eventClass = method.getParameterTypes()[0];
                method.setAccessible(true);
                listeners.computeIfAbsent(eventClass, k -> new ArrayList<>())
                        .add(new EventListener(object, method));
            }
        }
    }

    public void unregister(Object object) {
        for (List<EventListener> list : listeners.values()) {
            list.removeIf(l -> l.target() == object);
        }
    }

    public <T extends Event> T post(T event) {
        List<EventListener> list = listeners.get(event.getClass());
        if (list == null || list.isEmpty()) return event;
        for (EventListener listener : new ArrayList<>(list)) {
            try {
                listener.method().invoke(listener.target(), event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event;
    }

    private record EventListener(Object target, Method method) {}
}






