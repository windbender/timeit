<script>
   import {activeTabValue} from './tabs.js';
	import ButtonComponent from './ButtonComponent.svelte'
   import { guid  } from './guidstore.js'
   import {api_url} from './util.js'

   export function prettyDate(ts) {
      var dt = new Date(ts)
      let dts = ""+dt.getHours()+":"+("0" + dt.getMinutes()).slice(-2)+":"+("0" + dt.getSeconds()).slice(-2)
      return dts
   }

   var eventURL=api_url + 'api/event'

   var logar = []
   var number =''
   var log=''
   var event_name = ''
   $: $guid, guidChanged();

   const usp = new URLSearchParams(document.location.search);
   const new_guid = usp.get("guid");
   if(new_guid != null) {
      guid.update( old => new_guid )
      loadRecent()
   }
   var evtSource;

   
   function guidChanged() {
      if($guid.length < 4) return
      evtSource = new EventSource(eventURL+'/stream/'+$guid);
      
      evtSource.addEventListener("newFinish", (event) => {
         const msg = JSON.parse(event.data); 
         // shorter = logar.slice(Math.max(logar.length - 5, 1))
         const shorter = logar.slice(-4)
         logar = [...shorter,msg]
         console.log("got a typed message of ",msg);
      });
      
   }


   function addDigit(digit) {
      number = number + digit
   }

   async function loadRecent() {
      const res = await fetch(eventURL+'/'+$guid, {
         method: 'GET'         
      })
      if(!res.ok) {
         if(res.status == 404) {
            alert("Sorry, we don't recognize that event. Sending you to create a new one.")
            activeTabValue.update( out => 2 )

            return;
         } else {
           throw new Error(`HTTP error! status: ${res.status}`);
         }
      }
      var back = await res.json()
      const all_finishes = back.finishes

      console.log("all are ",all_finishes)
      const last_five = all_finishes.slice(Math.max(all_finishes.length - 5, 0))
      event_name = back.name
      document.title = event_name
      logar = last_five
      console.log("the last five are ",last_five)
   }
   async function finishRider() {
      if(number.length <1) return;
      let dt = new Date();
      let  time = dt.getTime();
      console.log("rider "+number+" finished at "+time);
      let dts = ""+dt.getHours()+":"+("0" + dt.getMinutes()).slice(-2)+":"+("0" + dt.getSeconds()).slice(-2)
      let finish = {rider:number,time:time};
      
      
      const res = await fetch(eventURL+'/'+$guid, {
         method: 'POST',
         headers: {
            "Content-Type": "application/json",
         },
         body: JSON.stringify({ ...finish})
      })
      number = '';

      console.log("recent finishers ",logar)
   }       
   function clearRider() {
      number = '';
   } 
</script>

<main>
   <div>
      event:  {event_name}
   </div>
<div>
<b><h1>#: {number}</h1></b>
</div>
<div>
<table>
<tr>
   <td><ButtonComponent on:click={() => addDigit(7)} symbol='7' bgColor={"#39c41f"}></ButtonComponent></td>
   <td><ButtonComponent on:click={() => addDigit(8)} symbol='8' bgColor={"#39c41f"}></ButtonComponent></td>
   <td><ButtonComponent on:click={() => addDigit(9)} symbol='9' bgColor={"#39c41f"}></ButtonComponent></td>
</tr>
<tr>
   <td><ButtonComponent on:click={() => addDigit(4)} symbol='4' bgColor={"#39c41f"}></ButtonComponent></td>
   <td><ButtonComponent on:click={() => addDigit(5)} symbol='5' bgColor={"#39c41f"}></ButtonComponent></td>
   <td><ButtonComponent on:click={() => addDigit(6)} symbol='6' bgColor={"#39c41f"}></ButtonComponent></td>
</tr><tr>
   <td><ButtonComponent on:click={() => addDigit(1)} symbol='1' bgColor={"#39c41f"}></ButtonComponent></td>
   <td><ButtonComponent on:click={() => addDigit(2)} symbol='2' bgColor={"#39c41f"}></ButtonComponent></td>
   <td><ButtonComponent on:click={() => addDigit(3)} symbol='3' bgColor={"#39c41f"}></ButtonComponent></td>
</tr>
<tr>
   <td></td>
   <td><ButtonComponent on:click={() => addDigit(0)} symbol='0' bgColor={"#39c41f"}></ButtonComponent></td>
   <td></td>
</tr>
</table>
</div>
<div style='margin-block-start: 8px;'>
   <ButtonComponent on:click={() => clearRider()} symbol='clear' bgColor={"#C4391f"}></ButtonComponent>
   <ButtonComponent on:click={() => finishRider()} symbol='FINISH!' bgColor={"#391fc4"}></ButtonComponent>
</div>
<div>
<table cellpadding='3'>
{#each logar as line} 
   <tr><td>{line.rider}</td><td>{prettyDate(line.time)}</td></tr>
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
