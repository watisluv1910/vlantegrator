import React, {createContext, useContext, useState} from 'react';

export const SIDEBAR_OPENED_WIDTH = 240;
export const SIDEBAR_COLLAPSED_WIDTH = 60;

const SidebarWidthContext = createContext({
    sidebarWidth: SIDEBAR_OPENED_WIDTH,
    setSidebarWidth: (amount) => {
        this.sidebarWidth = amount;
    }
});
const SidebarOpenContext = createContext({
    isOpen: true,
    setIsOpen: (state) => {
        this.isOpen = state;
    }
});

export const SidebarProvider = ({children, initialOpen = true}) => {
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

export const useSidebarWidth = () => {
    const context = useContext(SidebarWidthContext);
    if (!context) {
        throw new Error('useSidebar must be used within SidebarProvider');
    }
    return [context.sidebarWidth, context.setSidebarWidth];
};

export const useSidebarOpen = () => {
    const context = useContext(SidebarOpenContext);
    if (!context) {
        throw new Error('useSidebarOpen must be used within SidebarProvider');
    }
    return [context.isOpen, context.setIsOpen];
};