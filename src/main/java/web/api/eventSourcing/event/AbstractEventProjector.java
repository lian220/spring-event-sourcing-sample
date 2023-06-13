package web.api.eventSourcing.event;

import lombok.extern.slf4j.Slf4j;
import web.api.eventSourcing.query.Cart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public abstract class AbstractEventProjector implements EventProjector{

    protected static String APPLY_METHOD_NAME = "execute";

    @Override
    public void handle(Cart event) {
        Method method;
        try {
            method = this.getClass().getDeclaredMethod(APPLY_METHOD_NAME, event.getClass());
            if (method != null) {
                method.setAccessible(true);
                method.invoke(this, event);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
            log.warn(e.getMessage(), e);
            //throw new EventListenerNotApplyException(e.getMessage(), e);
        }
    }

    @Override
    public void handle(Event event) {

    }

}
