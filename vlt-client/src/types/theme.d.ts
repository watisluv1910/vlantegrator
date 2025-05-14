import '@mui/material/styles';

declare module '@mui/material/styles' {
    interface Palette {
        accent: Palette['primary'];
        white: Palette['primary'];
        secondaryAction: Palette['primary'];
        neutral: Palette['primary'];
        unavailable: Palette['primary'];
        console: Palette['primary'];
    }

    interface PaletteOptions {
        accent?: PaletteOptions['primary'];
        white?: PaletteOptions['primary'];
        secondaryAction?: PaletteOptions['primary'];
        neutral?: PaletteOptions['primary'];
        unavailable?: PaletteOptions['primary'];
        console?: PaletteOptions['primary'];
    }
}

declare module '@mui/material/Button' {
    interface ButtonPropsColorOverrides {
        accent: true;
        white: true;
        secondaryAction: true;
        neutral: true;
        unavailable: true;
        console: true;
    }
}

declare module '@mui/material/SvgIcon' {
    interface SvgIconPropsColorOverrides {
        accent: true;
        white: true;
        secondaryAction: true;
        neutral: true;
        unavailable: true;
        console: true;
    }
}