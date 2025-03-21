package plus.dragons.createcentralkitchen;

import com.mojang.logging.LogUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import plus.dragons.createcentralkitchen.entry.item.FDItemEntries;
import plus.dragons.createcentralkitchen.entry.item.FRItemEntries;
import plus.dragons.createcentralkitchen.foundation.config.CentralKitchenConfigs;
import plus.dragons.createcentralkitchen.foundation.data.CentralKitchenData;
import plus.dragons.createcentralkitchen.foundation.ponder.CentralKitchenPonders;
import plus.dragons.createcentralkitchen.foundation.resource.condition.ConfigBoolCondition;
import plus.dragons.createcentralkitchen.foundation.resource.condition.ConfigListCondition;
import plus.dragons.createcentralkitchen.foundation.utility.AutomaticModLoadSubscriber;
import plus.dragons.createcentralkitchen.foundation.utility.Mods;
import plus.dragons.createdragonlib.init.FillCreateItemGroupEvent;
import plus.dragons.createdragonlib.lang.Lang;

@Mod(CentralKitchen.ID)
public class CentralKitchen {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "create_central_kitchen";
    public static final String NAME = "Create: Central Kitchen";
    public static final Lang LANG = new Lang(ID);
    
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE_REGISTER =
        DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_REGISTER =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ID);
    
    public CentralKitchen() {
        CentralKitchenConfigs.register(ModLoadingContext.get());
        FMLModContainer container = (FMLModContainer) ModLoadingContext.get().getActiveContainer();
        AutomaticModLoadSubscriber.load(container, CentralKitchen.class);
        
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        REGISTRATE.registerEventListeners(modBus);
        RECIPE_TYPE_REGISTER.register(modBus);
        RECIPE_SERIALIZER_REGISTER.register(modBus);
        CraftingHelper.register(new ConfigBoolCondition.Serializer());
        CraftingHelper.register(new ConfigListCondition.Serializer());
        
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::fillItemGroup);
        
        if (DatagenModLoader.isRunningDataGen()) {
            CentralKitchenData.register(modBus);
        }
    }
    
    public void commonSetup(FMLCommonSetupEvent event) {
    
    }
    
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CentralKitchenPonders::register);
    }
    
    public void fillItemGroup(FillCreateItemGroupEvent event) {
        if (Mods.isLoaded(Mods.FD))
            event.addInsertion(AllBlocks.BLAZE_BURNER.get(), FDItemEntries.COOKING_GUIDE.asStack());
        if (Mods.isLoaded(Mods.FR))
            event.addInsertion(AllBlocks.BLAZE_BURNER.get(), FRItemEntries.BREWING_GUIDE.asStack());
//        if (Mods.isLoaded(Mods.MD))
//            event.addInsertion(AllBlocks.BLAZE_BURNER.get(), MDItemEntries.MINERS_COOKING_GUIDE.asStack());
    }
    
    public static ResourceLocation genRL(String path) {
        return new ResourceLocation(ID, path);
    }

}