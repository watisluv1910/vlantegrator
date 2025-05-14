import React, {
    createContext,
    Dispatch,
    ReactNode,
    SetStateAction,
    useContext,
    useState
} from "react";

export const SIDEBAR_OPENED_WIDTH: number = 240;
export const SIDEBAR_COLLAPSED_WIDTH: number = 60;

export type SidebarProviderProps = {
    children: ReactNode;
    initialOpen?: boolean;
};

export type SidebarWidthContextType = {
    sidebarWidth: number;
    setSidebarWidth: Dispatch<SetStateAction<number>>;
};

export type SidebarOpenContextType = {
    isOpen: boolean;
    setIsOpen: Dispatch<SetStateAction<boolean>>;
};

export const SidebarOpenContext = createContext<SidebarOpenContextType | undefined>(undefined);
export const SidebarWidthContext = createContext<SidebarWidthContextType | undefined>(undefined);

export const SidebarProvider = ({children, initialOpen = true}: SidebarProviderProps) => {
    const [isOpen, setIsOpen] = useState(initialOpen);
    const [sidebarWidth, setSidebarWidth] = useState(
        isOpen ? SIDEBAR_OPENED_WIDTH : SIDEBAR_COLLAPSED_WIDTH
    );

    React.useEffect(() => {
        setSidebarWidth(isOpen ? SIDEBAR_OPENED_WIDTH : SIDEBAR_COLLAPSED_WIDTH);
    }, [isOpen]);

    return (
        <SidebarOpenContext.Provider value={{isOpen, setIsOpen}}>
            <SidebarWidthContext.Provider value={{sidebarWidth, setSidebarWidth}}>
                {children}
            </SidebarWidthContext.Provider>
        </SidebarOpenContext.Provider>
    );
};

export const useSidebarWidth = (): [number, React.Dispatch<React.SetStateAction<number>>] => {
    const context = useContext(SidebarWidthContext);
    if (!context) {
        throw new Error("useSidebarWidth must be used within SidebarProvider");
    }
    return [context.sidebarWidth, context.setSidebarWidth];
};

export const useSidebarOpen = (): [boolean, Dispatch<SetStateAction<boolean>>] => {
    const context = useContext(SidebarOpenContext);
    if (!context) {
        throw new Error("useSidebarOpen must be used within SidebarProvider");
    }
    return [context.isOpen, context.setIsOpen];
};