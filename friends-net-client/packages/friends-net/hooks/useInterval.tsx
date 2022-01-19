import { useEffect, useRef } from "react";
import { clearInterval, setInterval } from "timers";

const useInterval = (callback: () => void, interval: number) => {

    const savedCallback = useRef<() => void>();

    useEffect(() => {
        savedCallback.current = callback;
    })

    useEffect(() => {
        const tick = () => savedCallback.current ? savedCallback.current() : undefined;

        const id = setInterval(tick, interval);
        return () => clearInterval(id);
    }, [interval]);
}

export default useInterval;