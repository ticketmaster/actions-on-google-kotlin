package actions.service.actionssdk.conversation.response.card

import actions.service.actionssdk.api.*

data class TableColumn(
        /**
         * Alias for `horizontalAlignment`
         *
         * Horizontal alignment of content w.r.t column. If unspecified, content
         * will be aligned to the leading edge.
         * @public
         */
        var align: GoogleActionsV2UiElementsTableCardColumnPropertiesHorizontalAlignment?,
        override var header: String?,
        override var horizontalAlignment: GoogleActionsV2UiElementsTableCardColumnPropertiesHorizontalAlignment?
) : GoogleActionsV2UiElementsTableCardColumnProperties

data class TableRow (
    /**
     * Cells in this row.
     * The first 3 cells are guaranteed to be shown but others might be cut on certain surfaces.
     * Please test with the simulator to see which cells will be shown for a given surface.
     *
     * When provided as a string array, creates the cells as text.
     * @public
     */
    var cells: MutableList<GoogleActionsV2UiElementsTableCardCell>? = null, //| string)[]
    /**
     * Indicates whether there should be a divider after each row.
     *
     * Overrides top level `dividers` property for this specific row if set.
     * @public
     */
    var dividerAfter: Boolean? = null
)

data class TableOptions(
        /**
         * Overall title of the table.
         *
         * Must be set if subtitle is set.
         * @public
         */
        var title: String? = null,

        /**
         * Subtitle for the table.
         * @public
         */
        var subtitle: String? = null,

        /**
         * Image associated with the table.
         * @public
         */
        var image: GoogleActionsV2UiElementsImage? = null,

        /**
         * Headers and alignment of columns with shortened name.
         * Alias of `columnProperties` with the additional capability of accepting a number type.
         *
         * This property or `columnProperties` is required.
         *
         * When provided as string array, just the header field is set per column.
         * When provided a number, it represents the number of elements per row.
         * @public
         */
        var columns: MutableList<TableColumn>? = null,//(TableColumn | string)[] | number

        /**
         * Headers and alignment of columns.
         *
         * This property or `columns` is required.
         *
         * When provided as string array, just the header field is set per column.
         * @public
         */
        var columnProperties: MutableList<TableColumn>? = null,//(TableColumn | string)[]

        /**
         * Row data of the table.
         *
         * The first 3 rows are guaranteed to be shown but others might be cut on certain surfaces.
         * Please test with the simulator to see which rows will be shown for a given surface.
         *
         * On surfaces that support the WEB_BROWSER capability, you can point the user to
         * a web page with more data.
         * @public
         */
        var rows: MutableList<TableRow>? = null, //(TableRow | string[])[]

        /**
         * Default dividerAfter for all rows.
         * Individual rows with `dividerAfter` set will override for that specific row.
         * @public
         */
        var dividers: Boolean? = null,

        /**
         * Buttons for the Table.
         * Currently at most 1 button is supported.
         * @public
         */
        var buttons: GoogleActionsV2UiElementsButton? = null //GoogleActionsV2UiElementsButton | Api.GoogleActionsV2UiElementsButton[]
)

//const toColumnProperties = (columns: (TableColumn | string)[]) => columns.map(column =>
//typeof column === 'string' ? {
//    header: column,
//} : {
//    header: column.header,
//    horizontalAlignment: column.horizontalAlignment || column.align,
//} as Api.GoogleActionsV2UiElementsTableCardColumnProperties)

