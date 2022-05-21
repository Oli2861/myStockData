package com.mystockdata.financialreportservice.xbrlfilings

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Contains retrieved information about a financial report.
 * @property reportPackage
 * @property date Reporting date
 * @property system Taxonomy used.
 * @property country
 * @property viewer Link to iXBLR-Viewer
 * @property xbrlJson Link to the xblrJson file
 * @property hasErrors Whether the report has errors.
 * @property hasWarnings Whether the report has warnings.
 * @property hasInconsistencies Whether the report has inconsistencies.
 * @property langs Languages
 * @property lei Legal Entity Identifier.
 * @property path Path to the file.
 * @property url Url to the financial report: Report URL = BaseURL + path + report-package (Report URL is constructed by using the base URL (https://filings.xbrl.org/) followed by the path and the report-package (= file name))
 */
data class RetrievedReportInfo(
    @JsonProperty("report-package")
    var reportPackage: String? = null,
    var date: Date? = null,
    var system: String? = null,
    var country: String? = null,
    var viewer: String? = null,
    @JsonProperty("xbrl-json")
    var xbrlJson: String? = null,
    var hasErrors: Boolean? = null,
    var hasWarnings: Boolean? = null,
    var hasInconsistencies: Boolean? = null,
    var langs: String? = null,
    var name: String? = null,
    var lei: String? = null,
    var path: String? = null,
    var url: String = "https://filings.xbrl.org/${path}/${reportPackage}"
)
