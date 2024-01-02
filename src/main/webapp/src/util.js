export function prettyDate(ts) {
    var dt = new Date(ts)
    let dts = ""+dt.getHours()+":"+("0" + dt.getMinutes()).slice(-2)+":"+("0" + dt.getSeconds()).slice(-2)
    return dts
 }

 export const api_url = document.location.origin + document.location.pathname

