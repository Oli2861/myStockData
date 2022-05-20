package com.mystockdata.financialreportservice.xbrlfilings

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Contains retrieved information about a financial report.
 * @property url Url to the financial report: Report URL = BaseURL + path + report-package (Report URL is constructed by using the base URL (https://filings.xbrl.org/) followed by the path and the report-package (= file name))
 */
data class RetrievedReportInfo(
    @JsonProperty("report-package")
    var reportPackage: String? = null,
    var date: String? = null,
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
