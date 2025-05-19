import {
    Storage as JdbcAdapterIcon,
    Http as HttpAdapterIcon,
    Transform as TransformerAdapterIcon,
    HistoryEdu as LoggerAdapterIcon,
} from "@mui/icons-material";
import type {OverridableComponent} from "@mui/material/OverridableComponent";
import type {SvgIconTypeMap} from "@mui/material";

export const adapterIconMap: Record<string, OverridableComponent<SvgIconTypeMap>> = {
    jdbc: JdbcAdapterIcon,
    http: HttpAdapterIcon,
    spELTransformer: TransformerAdapterIcon,
    logger: LoggerAdapterIcon,
};