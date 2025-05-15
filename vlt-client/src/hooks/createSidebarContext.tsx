import React, {
    Dispatch,
    PropsWithChildren,
    SetStateAction,
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";

interface SidebarState {
    isOpen: boolean;
    setIsOpen: Dispatch<SetStateAction<boolean>>;
    width: number;
    setWidth: Dispatch<SetStateAction<number>>;
}

export function createSidebarContext(options: { openedWidth: number; collapsedWidth: number; }) {
    const {openedWidth, collapsedWidth} = options;

    const StateContext = createContext<SidebarState | undefined>(undefined);

    const SidebarProvider: React.FC<PropsWithChildren<{
        initialOpen?: boolean;
    }>> = ({children, initialOpen = true}) => {
        const [isOpen, setIsOpen] = useState(initialOpen);
        const [width, setWidth] = useState(
            initialOpen ? openedWidth : collapsedWidth
        );

        useEffect(() => {
            setWidth(isOpen ? openedWidth : collapsedWidth);
        }, [isOpen]);

        return (
            <StateContext.Provider value={{isOpen, setIsOpen, width, setWidth}}>
                {children}
            </StateContext.Provider>
        );
    };

    function useSidebarOpen(): [boolean, Dispatch<SetStateAction<boolean>>] {
        const ctx = useContext(StateContext);
        if (!ctx) {
            throw new Error("useSidebarOpen must be used inside its SidebarProvider");
        }
        return [ctx.isOpen, ctx.setIsOpen];
    }

    function useSidebarWidth(): [number, Dispatch<SetStateAction<number>>] {
        const ctx = useContext(StateContext);
        if (!ctx) {
            throw new Error("useSidebarWidth must be used inside its SidebarProvider");
        }
        return [ctx.width, ctx.setWidth];
    }

    return {
        SidebarProvider,
        useSidebarOpen,
        useSidebarWidth,
    };
}