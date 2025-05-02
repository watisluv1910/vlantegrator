import {createTheme} from "@mui/material";

export const THEME = createTheme({
    cssVariables: {colorSchemeSelector: 'class'},
    defaultColorScheme: 'light',
    typography: {
        fontFamily: 'Ubuntu, sans-serif',
        h1: {fontSize: 42, fontWeight: 'bold'},
        h2: {fontSize: 30},
        h3: {fontSize: 26},
        h4: {fontSize: 20},
        h5: {fontSize: 18},
        h6: {fontSize: 16},
    },
    colorSchemes: {
        light: {
            palette: {
                background: {default: '#ffffff', paper: '#ffffff'},
                primary: {main: '#212b36'},
                secondary: {main: '#fa541c'},
                warning: {main: '#FFC107', contrastText: '#212121'},
                accent: {main: '#eabf46', contrastText: '#212121'},
                info: {main: '#f44336'},
                success: {main: '#4caf50'},
                error: {main: '#f44336'},
                white: {main: '#ffffff'},

                console: {main: '#e0e0e0'},
                secondaryAction: {main: '#eabf46'},
                neutral: {main: '#ffffff', contrastText: '#212121'},
                unavailable: {main: '#363636'},
            },
        },
        dark: {
            palette: {
                background: {default: '#171717', paper: '#212121'},
                primary: {main: '#ffffff'},
                secondary: {main: '#fa541c'},
                warning: {main: '#FFC107', contrastText: '#212121'},
                accent: {main: '#eabf46', contrastText: '#212121'},
                info: {main: '#f44336'},
                success: {main: '#388e3c'},
                error: {main: '#d32f2f'},
                white: {main: '#ffffff'},

                console: {main: '#212121'},
                secondaryAction: {main: '#eabf46'},
                neutral: {main: '#212121', contrastText: '#ffffff'},
                unavailable: {main: '#797979'},
            },
        },
    },
});