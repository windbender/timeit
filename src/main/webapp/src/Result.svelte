<script>
	import ButtonComponent from './ButtonComponent.svelte';
   import { prettyDate } from './util';
   import { guid  } from './guidstore.js';

   import {api_url} from './util.js'

   var eventURL=api_url + 'api/event'
   let event_name =""
   let finishes = []
   async function getResults() {

      var url = eventURL+"/processed/"+$guid;
      console.log("attempting" , url)
      const res = await fetch(url, {
         method: 'GET'
      })
      if(!res.ok) {
         throw new Error(`HTTP error! status: ${res.status}`);
      }
      var back = await res.json();
      event_name = back.name
      finishes = back.finishes;
   }       

   getResults();
</script>

<main>
   <div>{event_name}</div>

<div>
<table>
   <tr><th>rider</th><th>time</th></tr>
   {#each finishes as fin} 
   <tr><td>{fin.rider}</td><td>{prettyDate(fin.time)}</td></tr>
{/each}
</table>
</div>


</main>

<style>
        table {
          margin-left: auto;
          margin-right: auto;
        }
	main {
		text-align: center;
		padding: 3px;
		margin: 0 auto;
	}
	h1 {
                margin-block-start: 3px;
                margin-block-end: 3px;


		color: #ff3e00;
		text-transform: uppercase;
		font-size: 3em;
		font-weight: 300;
	}

	@media (min-width: 640px) {
		main {
			max-width: none;
		}
	}
</style>
